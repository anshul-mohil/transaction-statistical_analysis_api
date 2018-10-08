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
package com.anshul.controller;

import com.anshul.exception.FutureDatedOrNonParsableFieldException;
import com.anshul.exception.StaleTransactionException;
import com.anshul.service.ITransactionService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;


@Controller
public class TransactionController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);


  @Autowired
  private ITransactionService transactionService;

  @Produces("application/json")
  @Consumes("application/json")
  @RequestMapping(value = "/transactions", method = RequestMethod.OPTIONS)
  public ResponseEntity<?> makeTransaction(@RequestBody String jsonData) {
    LOGGER.debug("========= Inside makeTransaction() ============");
    try {
      transactionService.makeTransaction(jsonData);
    } catch (StaleTransactionException se) {
      LOGGER.info("<<===== Caught StaleTransactionException {}", se.getMessage());
      return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    } catch (FutureDatedOrNonParsableFieldException fe) {
      LOGGER.info("<<===== Caught FutureDatedOrNonParsableFieldException {}", fe.getMessage());
      return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
    } catch (JsonMappingException | JsonParseException je) {
      LOGGER.info("<<===== Caught JsonMappingException {}", je.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOGGER.error("<<===== Caught Exception {}", e.getMessage());
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Produces("application/json")
  @RequestMapping(value = "/transactions", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteTransaction() {
    LOGGER.debug("========= Inside deleteStatistics() ============");

    try {
      transactionService.deleteTransaction();
    } catch (Exception e) {
      TransactionController.LOGGER.error("<<===== Caught Exception {}", e.getMessage());
      return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
  }
}
