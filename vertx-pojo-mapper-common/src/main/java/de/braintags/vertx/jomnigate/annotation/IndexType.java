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
package de.braintags.vertx.jomnigate.annotation;

/**
 * Specifies a type of an index for an IndexField.
 * 
 * @author Michael Remme
 * 
 */
public enum IndexType {
  ASC(
      1),
  DESC(
      -1),
  GEO2D(
      "2d"),
  GEO2DSPHERE(
      "2dsphere"),
  TEXT(
      "text");

  private final Object direction;

  IndexType(final Object o) {
    direction = o;
  }

  public Object toIndexValue() {
    return direction;
  }

}
