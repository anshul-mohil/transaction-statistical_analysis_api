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
package com.anshul;

import com.anshul.model.StatisticsResource;
import com.anshul.model.TransactionResource;
import com.anshul.service.impl.StatisticsService;
import com.anshul.util.HeapNode;
import com.anshul.util.MinPriorityQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class StatisticsServiceTest {

  @InjectMocks
  private StatisticsService statisticsServiceMock;

  private MinPriorityQueue<?> minPriorityQueue;
  private Instant currentUTC;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    minPriorityQueue = MinPriorityQueue.heapNodeFactory;
    currentUTC = Instant.now();
  }

  @Test
  public void testGetStatisticsOnEmptyHeap() {
    StatisticsResource statisticsResource = new StatisticsResource();
    statisticsResource.setAvg("0.00");
    statisticsResource.setCount(0l);
    statisticsResource.setMax("0.00");
    statisticsResource.setMin("0.00");
    statisticsResource.setSum("0.00");
    ` `

    assertEquals(statisticsResource.toString(), minPriorityQueue.heapNodeFactory.getStatisticsFromHeap().toString());
  }

  @Test
  public void testGetStatistics() {
    StatisticsResource statisticsResource = new StatisticsResource();
    statisticsResource.setAvg("272.45");
    statisticsResource.setCount(3l);
    statisticsResource.setMax("427.39");
    statisticsResource.setMin("127.96");
    statisticsResource.setSum("817.36");

    addDataToHeap();

    assertEquals(statisticsResource.toString(), minPriorityQueue.heapNodeFactory.getStatisticsFromHeap().toString());
  }

  private void addDataToHeap() {
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("262.01");
    transactionResource.setTimestampOffset(-29900);

    minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))));

    transactionResource.setAmount("127.96");
    transactionResource.setTimestampOffset(-30000);

    minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))));

    transactionResource.setAmount("427.39");
    transactionResource.setTimestampOffset(-29600);

    minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))));

  }

}