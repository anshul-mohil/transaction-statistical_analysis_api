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

import com.anshul.exception.FutureDatedOrNonParsableFieldException;
import com.anshul.exception.StaleTransactionException;
import com.anshul.model.TransactionResource;
import com.anshul.service.impl.TransactionService;
import com.anshul.util.HeapNode;
import com.anshul.util.MinPriorityQueue;
import com.anshul.util.TransactionInputValidationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class TransactionServiceTest {

  private static final int MILLIS_TO_SUBTRACT = 60000;
  @Mock
  TransactionInputValidationUtil transactionInputValidationUtil;
  @InjectMocks
  private TransactionService transactionServiceMock;
  private MinPriorityQueue<HeapNode> minPriorityQueue;
  private Instant currentUTC;
  private Instant threshold;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    currentUTC = Instant.now();
    threshold = currentUTC.minusMillis(MILLIS_TO_SUBTRACT);
    minPriorityQueue = MinPriorityQueue.heapNodeFactory;
  }

  @After
  public void tearDown() throws Exception {
    transactionServiceMock = null;
    minPriorityQueue = null;
    currentUTC = null;
    threshold = null;
  }

  /**
   * Testing trasaction post request functionality.
   *
   * @throws IOException
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   */
  @Test
  public void testMakeTransaction()
      throws IOException, StaleTransactionException, FutureDatedOrNonParsableFieldException {
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("127.96");
    transactionResource.setTimestampOffset(-30000);

    transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);
    Instant txnTimeStamp = (transactionResource.getTimestamp() == null
        ? currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))
        : Instant.parse(transactionResource.getTimestamp()));

    assertEquals(true, minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp)));
  }

  /**
   * Testing Future dated scenario
   *
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   */
  @Test(expected = FutureDatedOrNonParsableFieldException.class)
  public void testFutureDatedTransactionException()
      throws StaleTransactionException, FutureDatedOrNonParsableFieldException {
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("827.96");
    transactionResource.setTimestampOffset(60003);

    Mockito.doThrow(new FutureDatedOrNonParsableFieldException("422", "future dated"))
        .when(transactionInputValidationUtil).validateInput(transactionResource, currentUTC, threshold);
    transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);

    Instant txnTimeStamp = (transactionResource.getTimestamp() == null
        ? currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))
        : Instant.parse(transactionResource.getTimestamp()));

    assertEquals(true, minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp)));
  }

  /**
   * Testing StaleTransactionException scenario
   *
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   */
  @Test(expected = StaleTransactionException.class)
  public void testStaleTransactionException()
      throws StaleTransactionException, FutureDatedOrNonParsableFieldException {
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("97.9446");
    transactionResource.setTimestampOffset(-160003);

    Mockito.doThrow(new StaleTransactionException("204", "timestamp is older than 60secs"))
        .when(transactionInputValidationUtil).validateInput(transactionResource, currentUTC, threshold);
    transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);

    Instant txnTimeStamp = (transactionResource.getTimestamp() == null
        ? currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))
        : Instant.parse(transactionResource.getTimestamp()));

    assertEquals(true, minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp)));
  }

  /**
   * Testing Non parsable Timestamp scenario
   *
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   */
  @Test(expected = FutureDatedOrNonParsableFieldException.class)
  public void testNonParsableException() throws StaleTransactionException, FutureDatedOrNonParsableFieldException {
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("827.96");
    transactionResource.setTimestamp("4/23/2018 11:32 PM");

    Mockito.doThrow(new FutureDatedOrNonParsableFieldException("422", "Non parseable timestamp"))
        .when(transactionInputValidationUtil).validateInput(transactionResource, currentUTC, threshold);
    transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);

    Instant txnTimeStamp = (transactionResource.getTimestamp() == null
        ? currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))
        : Instant.parse(transactionResource.getTimestamp()));

    assertEquals(true, minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp)));
  }

  /**
   * Testing JasonParseException scenario
   *
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   * @throws IOException
   */
  @Test(expected = JsonParseException.class)
  public void testJsonValidity()
      throws StaleTransactionException, FutureDatedOrNonParsableFieldException, IOException {
    String input = "Hello World!";
    TransactionResource transactionResource = new TransactionResource();
    transactionResource.setAmount("827.96");
    transactionResource.setTimestamp("4/23/2018 11:32 PM");

    Mockito.doThrow(new JsonParseException(null, input)).when(transactionInputValidationUtil)
        .transformJSONToObject(input);
    transactionInputValidationUtil.transformJSONToObject(input);

    transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);

    Instant txnTimeStamp = (transactionResource.getTimestamp() == null
        ? currentUTC.minusMillis(-(transactionResource.getTimestampOffset()))
        : Instant.parse(transactionResource.getTimestamp()));

    assertEquals(true, minPriorityQueue.heapNodeFactory
        .addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp)));
  }

  /**
   * Testing delete transaction functionality
   */
  @Test
  public void testDeleteTransactions() {

    minPriorityQueue.addElement(new HeapNode(10D, Instant.now().minusMillis(20000)));
    minPriorityQueue.addElement(new HeapNode(20D, Instant.now().minusMillis(30000)));
    minPriorityQueue.addElement(new HeapNode(30D, Instant.now().minusMillis(40000)));
    minPriorityQueue.addElement(new HeapNode(40D, Instant.now().minusMillis(50000)));
    minPriorityQueue.clearHeap();
    assertEquals(0, minPriorityQueue.size());
  }

}