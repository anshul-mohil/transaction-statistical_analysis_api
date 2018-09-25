package com.anshul.service;

import com.anshul.exception.FutureDatedOrNonParsableFieldException;
import com.anshul.exception.StaleTransactionException;

import java.io.IOException;

public interface ITransactionService {

  void makeTransaction(String jsonData) throws StaleTransactionException, FutureDatedOrNonParsableFieldException, IOException;

  void deleteTransaction();
}