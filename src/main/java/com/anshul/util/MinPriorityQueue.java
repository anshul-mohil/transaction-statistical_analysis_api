/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} Anshul Mohil
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.anshul.util;

import com.anshul.model.StatisticsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * MinPriorityQueue implementation using min heap.
 *
 * @param <T> Type to hold in internal Data Structure.
 */
public class MinPriorityQueue<T extends TransactionNode> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MinPriorityQueue.class);
  private static final long THRESHOLD_IN_MILLI = 60000;
  /**
   * Factory field to create cache with specific comparator strategy for creating
   * min heap.
   */
  public static MinPriorityQueue<HeapNode> heapNodeFactory = new MinPriorityQueue<>(new Comparator<HeapNode>() {
    @Override
    public int compare(HeapNode o1, HeapNode o2) {
      return o1.getInstant().compareTo(o2.getInstant());
    }
  });
  private final PriorityBlockingQueue<T> minHeap;

  private MinPriorityQueue(Comparator<T> comparator) {
    this.minHeap = new PriorityBlockingQueue<>(11, comparator);
  }

  /**
   * Method helps add new elements into collection
   *
   * @param node insert list of nodes
   * @return true if stored, if discard return false
   */
  public boolean addElement(T node) {
    boolean isAdded = false;
    try {
      isAdded = minHeap.add(node);
    } catch (OutOfMemoryError e) {
      LOGGER.info("Memory cleanup required before processing : ");
      cleanStaleData(Instant.now().minusMillis(THRESHOLD_IN_MILLI));
      try {
        isAdded = minHeap.add(node);
      } catch (OutOfMemoryError outOfMemoryError) {
        LOGGER.info("Unable to process more request due to memory full");
      }
    }
    return isAdded;
  }

  /**
   * Method helps to clear min heap
   */
  public void clearHeap() {
    minHeap.clear();
  }

  /**
   * @return current size of heap
   */
  public int size() {
    return minHeap.size();
  }

  /**
   * Method clean up cache holding data before timestampThreshold
   *
   * @param timestampThreshold THRESHOLD
   * @return number of stale data records removed
   */
  public int cleanStaleData(Instant timestampThreshold) {
    int count = 0;
    while (minHeap.peek() != null && timestampThreshold.isAfter(minHeap.peek().getInstant())) {
      minHeap.remove();
      count++;
    }
    return count;
  }

  /**
   * Method helps returning oldest timestamp in store.
   *
   * @return top element from min heap data structure
   * with lowest timestamp where lowest equals oldest..
   */
  public T getTopElement() {
    return minHeap.poll();
  }

  /**
   * Returned Statistics based on Eventually consistent data.
   *
   * @return StatisticsResource sr
   */
  public StatisticsResource getStatisticsFromHeap() {
    //multiple threads can get simultaneously
    StatisticsResource result = new StatisticsResource();

    /**
     * Thought process: Though PriorityBlockingQueue is threadsafe but
     * here get operation involves removal of stale objects. There would
     * be change where more then 2 threads try to remove same stale state
     * object and because of context switching one of them can face
     * NoSuchElementException.
     */
    synchronized (this) {
      // delete elements from priorityQueue older than threshold milli sec from now
      Instant timestampThreshold = Instant.now().minusMillis(THRESHOLD_IN_MILLI);

      int removedStaleDataCount = cleanStaleData(timestampThreshold);
      LOGGER.debug("count stale data records removed before heap traversal {} ", removedStaleDataCount);

      Iterator<T> itr = minHeap.iterator();
      BigDecimal sum = new BigDecimal(0);
      BigDecimal max = new BigDecimal(Integer.MIN_VALUE);
      BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
      while (itr.hasNext()) {
        T next = itr.next();
        BigDecimal amount = BigDecimal.valueOf(next.getAmount());
        sum = sum.add(amount);

        if (amount.compareTo(max) > 0) {
          max = amount;
        }
        if (amount.compareTo(min) < 0) {
          min = amount;
        }
      }
      if (minHeap.size() != 0) {
        BigDecimal avg = sum.divide(BigDecimal.valueOf(minHeap.size()), 2, RoundingMode.HALF_UP);

        result.setSum(String.valueOf(sum.setScale(2, RoundingMode.HALF_UP)));
        result.setAvg(String.valueOf(avg.setScale(2, RoundingMode.HALF_UP)));
        result.setMax(String.valueOf(max.setScale(2, RoundingMode.HALF_UP)));
        result.setMin(String.valueOf(min.setScale(2, RoundingMode.HALF_UP)));

        result.setCount(Long.valueOf(minHeap.size()));
      } else {
        result.setSum("0.00");
        result.setAvg("0.00");
        result.setMax("0.00");
        result.setMin("0.00");
        result.setCount(0l);
      }

    }
    return result;
  }


}
