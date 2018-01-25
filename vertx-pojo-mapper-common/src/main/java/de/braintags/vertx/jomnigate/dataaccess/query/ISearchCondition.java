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
package de.braintags.vertx.jomnigate.dataaccess.query;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.braintags.vertx.jomnigate.dataaccess.query.impl.FieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.GeoSearchArgument;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryAnd;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryNot;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryOr;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.VariableFieldCondition;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * The parts that make up the search condition of the query<br>
 * <br>
 * There are two possibilities to define an ISearchCondition:
 * either you are using IIndexedField as field specifyer or you are using a field name of the mapper. Typically
 * {@link IIndexedField} is defined as static final variable of a mapper and references to a certain field name of the
 * mapper. If at one time the field name changes for instance, you are sure that your defined queries are still working,
 * cause the content of the defined variable will be changed wither.<br/>
 * By using a String expression as field specifyer, you are more flexibled, but this version is more unsecure as well.
 * 
 * 
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 *
 * @author sschmitt
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ @JsonSubTypes.Type(value = QueryAnd.class, name = "and"),
    @JsonSubTypes.Type(value = QueryOr.class, name = "or"), @JsonSubTypes.Type(value = QueryNot.class, name = "not"),
    @JsonSubTypes.Type(value = FieldCondition.class, name = "condition"), })
public interface ISearchCondition {

  /**
   * Method validates the query arguments like field existence, to avoid wrong results
   * 
   * @param mapper
   */
  <T> void validate(IMapper<T> mapper);

  /**
   *
   * Create a query condition for the {@link QueryOperator#EQUALS} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition isEqual(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.EQUALS, value);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#EQUALS} operator with an unindexed field
   *
   * @param fieldName
   *          the name of the field
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition isEqual(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.EQUALS, value);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#EQUALS_IGNORE_CASE} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition isEqualIgnoreCase(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.EQUALS_IGNORE_CASE, value);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#EQUALS_IGNORE_CASE} operator with an unindexed field
   *
   * @param fieldName
   *          the name of the field
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition isEqualIgnoreCase(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.EQUALS_IGNORE_CASE, value);
  }

  /**
   *
   * Create a query condition for any given operator
   *
   * @param fieldName
   *          the name of the field for the comparison
   * @param operator
   *          any query operator
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition condition(final String fieldName, final QueryOperator operator, final Object value) {
    return createFieldCondition(fieldName, operator, value);
  }

  /**
   *
   * Create a query condition for any given operator
   *
   * @param field
   *          the field for the comparison
   * @param operator
   *          any query operator
   * @param value
   *          the value that the record must be equal to
   * @return
   */
  static IFieldCondition condition(final IIndexedField field, final QueryOperator operator, final Object value) {
    return createFieldCondition(field, operator, value);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#NOT_EQUALS} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must not be equal to
   * @return
   */
  static IFieldCondition notEqual(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.NOT_EQUALS, value);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#NOT_EQUALS} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must not be equal to
   * @return
   */
  static IFieldCondition notEqual(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.NOT_EQUALS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#LARGER} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must be larger than
   * @return
   */
  static IFieldCondition larger(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.LARGER, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#LARGER} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be larger than
   * @return
   */
  static IFieldCondition larger(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.LARGER, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#LARGER_EQUAL} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must be larger or equal to
   * @return
   */
  static IFieldCondition largerOrEqual(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.LARGER_EQUAL, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#LARGER_EQUAL} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be larger or equal to
   * @return
   */
  static IFieldCondition largerOrEqual(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.LARGER_EQUAL, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must be smaller than
   * @return
   */
  static IFieldCondition smaller(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.SMALLER, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be smaller than
   * @return
   */
  static IFieldCondition smaller(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.SMALLER, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER_EQUAL} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must be smaller or equal to
   * @return
   */
  static IFieldCondition smallerOrEqual(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.SMALLER_EQUAL, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER_EQUAL} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be smaller or equal to
   * @return
   */
  static IFieldCondition smallerOrEqual(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.SMALLER_EQUAL, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#IN} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition in(final String fieldName, final Object... values) {
    return in(fieldName, Arrays.asList(values));
  }

  /**
   * Create a query condition for the {@link QueryOperator#IN} operator
   *
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition in(final IIndexedField field, final Object... values) {
    return in(field, Arrays.asList(values));
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#IN} operator
   *
   * @param fieldName
   *          the name of the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition in(final String fieldName, final Collection<?> values) {
    return createFieldCondition(fieldName, QueryOperator.IN, values);
  }

  /**
   *
   * Create a query condition for the {@link QueryOperator#IN} operator
   *
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition in(final IIndexedField field, final Collection<?> values) {
    return createFieldCondition(field, QueryOperator.IN, values);
  }

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition notIn(final String fieldName, final Object... values) {
    return notIn(fieldName, Arrays.asList(values));
  }

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   *
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition notIn(final IIndexedField field, final Object... values) {
    return notIn(field, Arrays.asList(values));
  }

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   *
   * @param fieldName
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition notIn(final String fieldName, final Collection<?> values) {
    return createFieldCondition(fieldName, QueryOperator.NOT_IN, values);
  }

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   *
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  static IFieldCondition notIn(final IIndexedField field, final Collection<?> values) {
    return createFieldCondition(field, QueryOperator.NOT_IN, values);
  }

  /**
   * Create a query condition for the {@link QueryOperator#STARTS} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param value
   *          the value that the record must start with
   * @return
   */
  static IFieldCondition startsWith(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.STARTS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#STARTS} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must start with
   * @return
   */
  static IFieldCondition startsWith(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.STARTS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#ENDS} operator
   *
   * @param fieldName
   *          the name of the field for the comparison
   * @param value
   *          the value that the record must end with
   * @return
   */
  static IFieldCondition endsWith(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.ENDS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#ENDS} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must end with
   * @return
   */
  static IFieldCondition endsWith(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.ENDS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#CONTAINS} operator
   *
   * @param fieldName
   *          the name of the field for the comparison
   * @param value
   *          the value that must be contained
   * @return
   */
  static IFieldCondition contains(final String fieldName, final Object value) {
    return createFieldCondition(fieldName, QueryOperator.CONTAINS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#CONTAINS} operator
   *
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that must be contained
   * @return
   */
  static IFieldCondition contains(final IIndexedField field, final Object value) {
    return createFieldCondition(field, QueryOperator.CONTAINS, value);
  }

  /**
   * Create a query condition for the {@link QueryOperator#NEAR} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param longitude
   *          the longitude geo coordinate
   * @param latitude
   *          the latitude geo coordinate
   * @return
   */
  static IFieldCondition near(final String fieldName, final double longitude, final double latitude) {
    return createFieldCondition(fieldName, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(longitude, latitude, new double[0]))));
  }

  /**
   * Create a query condition for the {@link QueryOperator#NEAR} operator
   *
   * @param field
   *          the field for the comparison
   * @param longitude
   *          the longitude coordinate
   * @param latitude
   *          the latitude coordinate
   * @return
   */
  static IFieldCondition near(final IIndexedField field, final double longitude, final double latitude) {
    // don't change order of longitude/latitude, or else mongo doesn't work anymore
    return createFieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(longitude, latitude, new double[0]))));
  }

  /**
   * Create a query condition for the {@link QueryOperator#NEAR} operator
   *
   * @param fieldName
   *          the field name for the comparison
   * @param longitude
   *          the longitude geo coordinate
   * @param latitude
   *          the latitude geo coordinate
   * @param maxDistance
   *          the maximum distance to the given point
   * @return
   */
  static IFieldCondition near(final String fieldName, final double longitude, final double latitude, final int maxDistance) {
    return createFieldCondition(fieldName, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(longitude, latitude, new double[0])), maxDistance));
  }

  /**
   * Create a query condition for the {@link QueryOperator#NEAR} operator
   *
   * @param field
   *          the field for the comparison
   * @param longitude
   *          the longitude coordinate
   * @param latitude
   *          the latitude coordinate
   * @param maxDistance
   *          the maximum distance in meters to the given point
   * @return
   */
  static IFieldCondition near(final IIndexedField field, final double longitude, final double latitude, final int maxDistance) {
    // don't change order of longitude/latitude, or else mongo doesn't work anymore
    return createFieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(longitude, latitude, new double[0])), maxDistance));
  }

  /**
   * Create a new field condition object with the given values. Checks if the value is a variable. If yes, creates a
   * {@link VariableFieldCondition} to replace the variable with its actual value during execution.
   *
   * @param fieldName
   *          the field of the condition
   * @param operator
   *          the logic operator for the condition
   * @param value
   *          the value of the condition
   * @return a new field condition object
   */
  static IFieldCondition createFieldCondition(final String fieldName, final QueryOperator operator, final Object value) {
    return createFieldCondition(IIndexedField.create(fieldName), operator, value);
  }

  /**
   * Create a new field condition object with the given values. Checks if the value is a variable. If yes, creates a
   * {@link VariableFieldCondition} to replace the variable with its actual value during execution.
   *
   * @param field
   *          the field of the condition
   * @param operator
   *          the logic operator for the condition
   * @param value
   *          the value of the condition
   * @return a new field condition object
   */
  static IFieldCondition createFieldCondition(final IIndexedField field, final QueryOperator operator, final Object value) {
    if (value instanceof String && StringUtils.isNotBlank((String) value)) {
      String stringValue = (String) value;
      if (stringValue.startsWith("${") && stringValue.endsWith("}")) {
        return new VariableFieldCondition(field, operator, stringValue.substring(2, stringValue.length() - 1));
      }
    }
    return new FieldCondition(field, operator, value);
  }

  /**
   * Connects the given query parts with the {@link QueryLogic#AND} connector
   *
   * @param searchConditions
   *          the search conditions to connect
   * @return
   */
  static ISearchConditionContainer and(final ISearchCondition... searchConditions) {
    return new QueryAnd(searchConditions);
  }

  /**
   * Connects the given query parts with the {@link QueryLogic#AND} connector
   *
   * @param searchConditions
   *          the search conditions to connect
   * @return
   */
  static ISearchConditionContainer and(final Collection<ISearchCondition> searchConditions) {
    return new QueryAnd(new ISearchCondition[searchConditions.size()]);
  }

  /**
   * Connects the given query parts with the {@link QueryLogic#OR} connector
   *
   * @param searchConditions
   *          the search conditions to connect
   * @return
   */
  static ISearchConditionContainer or(final ISearchCondition... searchConditions) {
    return new QueryOr(searchConditions);
  }

  /**
   * Connects the given query parts with the {@link QueryLogic#OR} connector
   *
   * @param searchConditions
   *          the search conditions to connect
   * @return
   */
  static ISearchConditionContainer or(final Collection<ISearchCondition> searchConditions) {
    return new QueryOr(new ISearchCondition[searchConditions.size()]);
  }

  /**
   * Negates the given query part with the {@link QueryLogic#NOT} operator
   *
   * @param searchCondition
   *          the search condition to negate
   * @return
   */
  static ISearchConditionContainer not(final ISearchCondition searchCondition) {
    return new QueryNot(searchCondition);
  }

}
