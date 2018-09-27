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

import java.time.Instant;
import java.util.Objects;

public final class HeapNode implements TransactionNode {

  private Double amount;
  private Instant instant;

  public HeapNode(Double amount, Instant instant) {
    this.amount = amount;
    this.instant = instant;
  }

  @Override
  public Double getAmount() {
    return amount;
  }

  @Override
  public Instant getInstant() {
    return instant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    HeapNode heapNode = (HeapNode) o;
    return Objects.equals(this.getAmount(), heapNode.getAmount()) &&
        Objects.equals(this.getInstant(), heapNode.getInstant());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAmount(), getInstant());
  }

  @Override
  public String toString() {
    return "HeapNode{" +
        "amount=" + amount +
        ", instant=" + instant +
        '}';
  }
}
