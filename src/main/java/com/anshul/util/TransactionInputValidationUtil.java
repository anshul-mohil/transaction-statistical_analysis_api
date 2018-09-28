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

import com.anshul.exception.FutureDatedOrNonParsableFieldException;
import com.anshul.exception.StaleTransactionException;
import com.anshul.model.TransactionResource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

//TODO; Add object context in logging
@Component
public class TransactionInputValidationUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInputValidationUtil.class);

  public void validateInput(TransactionResource transactionResource, Instant currentUTC, Instant threshold)
      throws StaleTransactionException, FutureDatedOrNonParsableFieldException {
    LOGGER.debug("============= Inside validateInput ============");

    Instant transactionTimeStamp = null;

    try {
      if (transactionResource.getTimestamp() == null && transactionResource.getTimestampOffset() == 0) {
        throw new Exception();
      }
      if (transactionResource.getTimestamp() != null) {
        transactionTimeStamp = Instant.parse(transactionResource.getTimestamp());
      }
      Double.parseDouble(transactionResource.getAmount());
    } catch (Exception exception) {
      throw new FutureDatedOrNonParsableFieldException("422", "Non parsable field");
    }

    if (transactionResource.getTimestampOffset() < 0) {
      transactionTimeStamp = currentUTC.minusMillis(-(transactionResource.getTimestampOffset()));
      LOGGER.debug("====== transactionTimeStamp: {}, amount:  {} ==========", transactionTimeStamp, transactionResource.getAmount());
    }
    if (transactionTimeStamp != null && transactionTimeStamp.compareTo(threshold) <= -1) {
      LOGGER.debug("============ Stale data, timestamp: {} ===========", transactionTimeStamp);
      throw new StaleTransactionException("204", "timestamp is older than 60secs");
    }

    if ((transactionTimeStamp != null && transactionTimeStamp.compareTo(currentUTC) >= 1)
        || transactionResource.getTimestampOffset() > 0) {
      LOGGER.debug("============ future dated transaction, timestamp: {}", transactionTimeStamp);
      throw new FutureDatedOrNonParsableFieldException("422", "it is future dated transaction");
    }
  }

  public TransactionResource transformJSONToObject(String jsonData) throws IOException {
    LOGGER.debug("======================= Inside transformJSONToObject======================");
    ObjectMapper mapper = new ObjectMapper();
    TransactionResource resource = null;
    try {
      // JSON from String to Object
      resource = mapper.readValue(jsonData, TransactionResource.class);
    } catch (JsonMappingException | JsonParseException je) {
      LOGGER.info("json exception" + je);
      throw je;
    } catch (IOException ie) {
      LOGGER.info("json io exception" + ie);
      throw ie;
    } catch (Exception e) {
      LOGGER.info("exception" + e);
      throw e;
    }
    return resource;
  }

}
