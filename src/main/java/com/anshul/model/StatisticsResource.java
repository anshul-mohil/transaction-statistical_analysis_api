package com.anshul.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatisticsResource {

  @JsonProperty("sum")
  private String sum;

  @JsonProperty("avg")
  private String avg;

  @JsonProperty("max")
  private String max;

  @JsonProperty("min")
  private String min;

  @JsonProperty("count")
  private Long count;

  public String getSum() {
    return sum;
  }

  public void setSum(String sum) {
    this.sum = sum;
  }

  public String getAvg() {
    return avg;
  }

  public void setAvg(String avg) {
    this.avg = avg;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "StatisticsResource{" +
        "sum=" + sum +
        ", avg=" + avg +
        ", max=" + max +
        ", min=" + min +
        ", count=" + count +
        '}';
  }
}
