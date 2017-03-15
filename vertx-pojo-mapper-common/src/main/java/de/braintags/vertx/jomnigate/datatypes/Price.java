/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An extension of {@link BigDecimal} to handle Prices
 * 
 * @author mremme
 * 
 */
public class Price extends BigDecimal {

  /**
   * @param val
   */
  @JsonCreator
  public Price(BigDecimal val) {
    super(val.doubleValue());
  }

  /**
   * @param in
   */
  public Price(char[] in) {
    super(in);
  }

  /**
   * @param val
   */
  public Price(String val) {
    super(val);
  }

  /**
   * @param val
   */
  public Price(double val) {
    super(val);
  }

  /**
   * @param val
   */
  public Price(BigInteger val) {
    super(val);
  }

  /**
   * @param val
   */
  public Price(int val) {
    super(val);
  }

  /**
   * @param val
   */
  public Price(long val) {
    super(val);
  }

  /**
   * @param in
   * @param mc
   */
  public Price(char[] in, MathContext mc) {
    super(in, mc);
  }

  /**
   * @param val
   * @param mc
   */
  public Price(String val, MathContext mc) {
    super(val, mc);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public Price(double arg0, MathContext arg1) {
    super(arg0, arg1);
  }

  /**
   * @param val
   * @param mc
   */
  public Price(BigInteger val, MathContext mc) {
    super(val, mc);
  }

  /**
   * @param unscaledVal
   * @param scale
   */
  public Price(BigInteger unscaledVal, int scale) {
    super(unscaledVal, scale);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public Price(int arg0, MathContext arg1) {
    super(arg0, arg1);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public Price(long arg0, MathContext arg1) {
    super(arg0, arg1);
  }

  /**
   * @param in
   * @param offset
   * @param len
   */
  public Price(char[] in, int offset, int len) {
    super(in, offset, len);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   */
  public Price(BigInteger arg0, int arg1, MathContext arg2) {
    super(arg0, arg1, arg2);
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   */
  public Price(char[] arg0, int arg1, int arg2, MathContext arg3) {
    super(arg0, arg1, arg2, arg3);
  }

}
