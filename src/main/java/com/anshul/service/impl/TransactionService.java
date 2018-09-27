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
package com.anshul.service.impl;

import com.anshul.exception.FutureDatedOrNonParsableFieldException;
import com.anshul.exception.StaleTransactionException;
import com.anshul.model.TransactionResource;
import com.anshul.service.ITransactionService;
import com.anshul.util.HeapNode;
import com.anshul.util.MinPriorityQueue;
import com.anshul.util.TransactionInputValidationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

/**
 * TransactionService helps insert and delete
 * transactions/data from heap.
 */
@Service("transactionService")
public class TransactionService implements ITransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
  private static final int MILLIS_TO_SUBTRACT = 60000;

  @Autowired
  TransactionInputValidationUtil transactionInputValidationUtil;

  /**
   * Method helps insert transactions into cache
   * after validating input. If input is not as expected
   * it will throw appropriate exception.
   *
   * @param jsonData String input data from request
   * @throws StaleTransactionException
   * @throws FutureDatedOrNonParsableFieldException
   * @throws IOException
   */
  @Override
  public void makeTransaction(String jsonData)
      throws StaleTransactionException, FutureDatedOrNonParsableFieldException, IOException {
    LOGGER.debug("========= Inside TransactionService makeTransaction() ============");

    Instant currentUTC = Instant.now();
    Instant threshold = currentUTC.minusMillis(MILLIS_TO_SUBTRACT);

    try {
      TransactionResource transactionResource = transactionInputValidationUtil.transformJSONToObject(jsonData);

      transactionInputValidationUtil.validateInput(transactionResource, currentUTC, threshold);

      Instant txnTimeStamp = (transactionResource.getTimestamp() == null ?
          currentUTC.minusMillis(-(transactionResource.getTimestampOffset())) : Instant.parse(transactionResource.getTimestamp()));

      MinPriorityQueue.heapNodeFactory.addElement(new HeapNode(Double.valueOf(transactionResource.getAmount()), txnTimeStamp));

    } catch (JsonMappingException | JsonParseException je) {
      throw je;
    } catch (StaleTransactionException se) {
      throw se;
    } catch (FutureDatedOrNonParsableFieldException fe) {
      throw fe;
    }

  }

  /**
   * Method helps clearing entire cache.
   */
  @Override
  public void deleteTransaction() {
    LOGGER.debug("========= Inside TransactionService deleteTransaction() ============");
    MinPriorityQueue.heapNodeFactory.clearHeap();
  }
}
