/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.typehandler.impl;

import de.braintags.io.vertx.pojomapper.typehandler.IFieldParameterResult;

/**
 * The default implementation of {@link IFieldParameterResult}
 * 
 * @author Michael Remme
 * 
 */
public class DefaultFieldParameterResult implements IFieldParameterResult {
  private final String colName;
  private final String operator;
  private final Object value;

  /**
   * @param colName
   * @param operator
   * @param value
   */
  public DefaultFieldParameterResult(String colName, String operator, Object value) {
    this.colName = colName;
    this.operator = operator;
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.IFieldParameterResult#getColName()
   */
  @Override
  public String getColName() {
    return colName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.IFieldParameterResult#getOperator()
   */
  @Override
  public String getOperator() {
    return operator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.IFieldParameterResult#getValue()
   */
  @Override
  public Object getValue() {
    return value;
  }

}
