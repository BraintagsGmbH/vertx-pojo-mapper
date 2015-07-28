/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;

/**
 * A simple mapper with some beans properties
 *
 * @author Michael Remme
 * 
 */

public class SimpleMapper {
  @Id
  public String id;
  public String name;
  private String secondProperty;
  public int intValue;

  /**
   * 
   */
  public SimpleMapper() {
  }

  /**
   * 
   */
  public SimpleMapper(String name, String secondProperty) {
    this.name = name;
    this.secondProperty = secondProperty;
  }

  /**
   * @return the secondProperty
   */
  public final String getSecondProperty() {
    return secondProperty;
  }

  /**
   * @param secondProperty
   *          the secondProperty to set
   */
  public final void setSecondProperty(String secondProperty) {
    this.secondProperty = secondProperty;
  }

  @Override
  public boolean equals(Object o) {
    SimpleMapper compare = (SimpleMapper) o;

    return o != null && compare.id.equals(id) && compare.name.equals(name)
        && compare.secondProperty.equals(secondProperty);
  }

  @Override
  public String toString() {
    return String.valueOf(name);
  }
}
