package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.Arrays;
import java.util.Collection;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;

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

  private FieldCondition(String field, QueryOperator logic, Object value) {
    this.field = field;
    this.operator = logic;
    this.value = value;
  }

  public static FieldCondition isEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.EQUALS, value);
  }

  public static FieldCondition notEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.NOT_EQUALS, value);
  }

  public static FieldCondition larger(String field, Object value) {
    return new FieldCondition(field, QueryOperator.LARGER, value);
  }

  public static FieldCondition largerOrEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.LARGER_EQUAL, value);
  }

  public static FieldCondition smaller(String field, Object value) {
    return new FieldCondition(field, QueryOperator.SMALLER, value);
  }

  public static FieldCondition smallerOrEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.SMALLER_EQUAL, value);
  }

  public static FieldCondition in(String field, Object... values) {
    return in(field, Arrays.asList(values));
  }

  public static FieldCondition in(String field, Collection<?> values) {
    return new FieldCondition(field, QueryOperator.IN, values);
  }

  public static FieldCondition notIn(String field, Object... values) {
    return notIn(field, Arrays.asList(values));
  }

  public static FieldCondition notIn(String field, Collection<?> values) {
    return new FieldCondition(field, QueryOperator.NOT_IN, values);
  }

  public static FieldCondition startsWith(String field, Object value) {
    return new FieldCondition(field, QueryOperator.STARTS, value);
  }

  public static FieldCondition endsWith(String field, Object value) {
    return new FieldCondition(field, QueryOperator.ENDS, value);
  }

  public static FieldCondition contains(String field, Object value) {
    return new FieldCondition(field, QueryOperator.CONTAINS, value);
  }

  public static FieldCondition near(String field, double x, double y, int maxDistance) {
    return new FieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(x, y, new double[0])), maxDistance));
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
