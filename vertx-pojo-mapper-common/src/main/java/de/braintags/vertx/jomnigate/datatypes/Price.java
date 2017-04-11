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

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An extension of {@link BigDecimal} to handle Prices
 * 
 * @author mremme
 * 
 */
public class Price extends BigDecimal {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

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

}
