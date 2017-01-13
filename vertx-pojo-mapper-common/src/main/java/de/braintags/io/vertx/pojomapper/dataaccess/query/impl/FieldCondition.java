/*-
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
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import org.apache.commons.lang3.StringUtils;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import io.vertx.codegen.annotations.Nullable;

/**
 * Simple implementation of {@link IFieldCondition}<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public class FieldCondition implements IFieldCondition {

  private String field;
  private QueryOperator operator;
  private Object value;

  /**
   * Creates a complete field condition
   * 
   * @param field
   *          the field of this condition
   * @param logic
   *          the compare logic of this condition
   * @param value
   *          the value of this condition, can be null
   */
  public FieldCondition(String field, QueryOperator logic, @Nullable Object value) {
    this.field = field;
    this.operator = logic;
    this.value = value;
  }

  /**
   * @return the POJO field of this condition
   */
  @Override
  public String getField() {
    return field;
  }

  /**
   * @return the compare logic of this condition
   */
  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  /**
   * @return the value of this condition, can be null
   */
  @Override
  public Object getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return field + " " + operator + " "
        + (value instanceof Iterable<?> ? "(" + StringUtils.join((Iterable<?>) value, ",") + ")" : value);
  }

}
