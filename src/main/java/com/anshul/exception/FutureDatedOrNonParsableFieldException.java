package com.anshul.exception;

public class FutureDatedOrNonParsableFieldException extends Exception {

  private static final long serialVersionUID = 1L;

  private String code;

  private String errorMessage;

  public FutureDatedOrNonParsableFieldException(String code, String errorMessage) {
    super();
    this.code = code;
    this.errorMessage = errorMessage;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
