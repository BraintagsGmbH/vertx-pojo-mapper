package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

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

  private String        field;
  private QueryOperator operator;
  private Object        value;

  public FieldCondition(String field, Object value) {
    this(field, QueryOperator.EQUALS, value);
  }

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
}
