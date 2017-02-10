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

import de.braintags.vertx.jomnigate.dataaccess.query.impl.FieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.GeoSearchArgument;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryAnd;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryOr;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.VariableFieldCondition;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;

/**
 * The parts that make up the search condition of the query<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 *
 * @author sschmitt
 */
public interface ISearchCondition {

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
   static IFieldCondition isEqual(String field, Object value) {
     return createFieldCondition(field, QueryOperator.EQUALS, value);
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
   static IFieldCondition condition(String field, QueryOperator operator, Object value) {
     return createFieldCondition(field, operator, value);
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
   static IFieldCondition notEqual(String field, Object value) {
     return createFieldCondition(field, QueryOperator.NOT_EQUALS, value);
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
   static IFieldCondition larger(String field, Object value) {
     return createFieldCondition(field, QueryOperator.LARGER, value);
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
   static IFieldCondition largerOrEqual(String field, Object value) {
     return createFieldCondition(field, QueryOperator.LARGER_EQUAL, value);
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
   static IFieldCondition smaller(String field, Object value) {
     return createFieldCondition(field, QueryOperator.SMALLER, value);
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
   static IFieldCondition smallerOrEqual(String field, Object value) {
     return createFieldCondition(field, QueryOperator.SMALLER_EQUAL, value);
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
   static IFieldCondition in(String field, Object... values) {
     return in(field, Arrays.asList(values));
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
   static IFieldCondition in(String field, Collection<?> values) {
     return createFieldCondition(field, QueryOperator.IN, values);
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
   static IFieldCondition notIn(String field, Object... values) {
     return notIn(field, Arrays.asList(values));
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
   static IFieldCondition notIn(String field, Collection<?> values) {
     return createFieldCondition(field, QueryOperator.NOT_IN, values);
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
   static IFieldCondition startsWith(String field, Object value) {
     return createFieldCondition(field, QueryOperator.STARTS, value);
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
   static IFieldCondition endsWith(String field, Object value) {
     return createFieldCondition(field, QueryOperator.ENDS, value);
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
   static IFieldCondition contains(String field, Object value) {
     return createFieldCondition(field, QueryOperator.CONTAINS, value);
   }

  /**
    * Create a query condition for the {@link QueryOperator#NEAR} operator
    *
    * @param field
    *          the field for the comparison
    * @param x
    *          the x geo coordinate
    * @param y
    *          the y geo coordinate
    * @param maxDistance
    *          the maximum distance to the given point
    * @return
    */
   static IFieldCondition near(String field, double x, double y, int maxDistance) {
     return createFieldCondition(field, QueryOperator.NEAR,
         new GeoSearchArgument(new GeoPoint(new Position(x, y, new double[0])), maxDistance));
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
   static IFieldCondition createFieldCondition(String field, QueryOperator operator, Object value) {
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
   static ISearchConditionContainer and(ISearchCondition... searchConditions) {
     return new QueryAnd(searchConditions);
   }

  /**
    * Connects the given query parts with the {@link QueryLogic#OR} connector
    *
    * @param searchConditions
    *          the search conditions to connect
    * @return
    */
   static ISearchConditionContainer or(ISearchCondition... searchConditions) {
     return new QueryOr(searchConditions);
   }

}
