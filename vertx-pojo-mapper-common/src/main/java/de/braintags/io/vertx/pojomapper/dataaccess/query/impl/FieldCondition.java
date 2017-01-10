package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import org.apache.commons.lang3.StringUtils;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;

/**
 * <br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public class FieldCondition implements IQueryCondition {

  private String field;
  private QueryOperator operator;
  private Object value;

  public FieldCondition(String field, QueryOperator logic, Object value) {
    this.field = field;
    this.operator = logic;
    this.value = value;
  }

  /**
   * @return the field
   */
  @Override
  public String getField() {
    return field;
  }

  /**
   * @return the logic
   */
  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  /**
   * @return the value
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
