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

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class FieldParameter<T extends IQueryContainer> implements IFieldParameter<T> {
  private IField field;
  private Object value;
  private QueryOperator operator;
  private T container;

  /**
   * 
   */
  public FieldParameter(T container, IField field) {
    this.container = container;
    this.field = field;
  }

  @Override
  public IField getField() {
    return field;
  }

  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public T is(Object value) {
    return addValue(QueryOperator.is, container);
  }

  @Override
  public T contains(Object value) {
    return addValue(QueryOperator.contains, container);
  }

  private T addValue(QueryOperator op, Object value) {
    this.operator = op;
    this.value = value;
    return container;
  }

}
