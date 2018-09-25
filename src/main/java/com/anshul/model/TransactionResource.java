package com.anshul.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResource {

  @JsonProperty("amount")
  private String amount;

  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("_timestampOffset")
  private long timestampOffset;

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public long getTimestampOffset() {
    return timestampOffset;
  }

  public void setTimestampOffset(long timestampOffset) {
    this.timestampOffset = timestampOffset;
  }

  @Override
  public String toString() {
    return "TransactionResource{" +
        "amount='" + amount + '\'' +
        ", timestamp='" + timestamp + '\'' +
        ", timestampOffset=" + timestampOffset +
        '}';
  }
}