/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query;

import java.util.Collection;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Define and execute queries inside the connected {@link IDataStore}
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper class
 */

public interface IQuery<T> extends IDataAccessObject<T> {

  /**
   * Execute the query
   * 
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Execute the query by counting the fitting objects
   * 
   * @param resultHandler
   *          contains the {@link IQueryCountResult}
   */
  void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler);

  /**
   * Execute the query with the option explain and sends back the suitable information
   * 
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Set the maximum number of records to be returned
   * 
   * @param limit
   *          the maximum number of records to be returned
   * @return the query itself for fluent access
   */
  IQuery<T> setLimit(int limit);

  /**
   * Set the first record to be returned
   * 
   * @param start
   *          the number of the first record to be returned
   * @return the query itself for fluent access
   */
  IQuery<T> setStart(int start);

  /**
   * If {@link #setLimit(int)} is defined with a value > 0 and this value is set to true, then the
   * {@link IQueryResult#getCompleteResult()} will return the fitting value
   * 
   * @param returnCompleteCount
   *          true, if complete count of selection shall be returned ( which might result into a double query count )
   * @return the query itself for fluent access
   */
  IQuery<T> setReturnCompleteCount(boolean returnCompleteCount);

  /**
   * Add a field to sort the resulting selection by. This method is the same than addSort( fieldName, true )
   * 
   * @param sortField
   *          the field, by which to sort the selection
   * @return an instance of {@link ISortDefinition} for fluent access
   */
  ISortDefinition<T> addSort(String sortField);

  /**
   * Add a field to sort the resulting selection by. This method is the same than addSort( fieldName, true )
   * 
   * @param sortField
   *          the field, by which to sort the selection
   * @param ascending
   *          true, if sort shall be ascending
   * @return an instance of {@link ISortDefinition} for fluent access
   */
  ISortDefinition<T> addSort(String sortField, boolean ascending);

  /**
   * Add a command, which is a database specific object like an sql string or a JsonObject for mongo. If a native
   * command is added, other parameters are ignored on execution. When the IQuery is executed later, then the native
   * command is executed and the result is transformed into the POJOs of the defined mapper class
   * 
   * @param command
   *          the command to be executed
   */
  void setNativeCommand(Object command);

  /**
   * Get a formerly defined native command
   * 
   * @return the command or null, if none defined
   */
  Object getNativeCommand();

  /**
   * Returns true, if the current query definition contains arguments
   * 
   * @return
   */
  boolean hasQueryArguments();

  /**
   * Set the complete search condition of the query
   * 
   * @param queryPart
   *          the root query part that contains or builds the complete query search condition
   */
  void setRootQueryPart(IQueryPart queryPart);

  /**
   * Get the complete search condition of the query
   * 
   * @return the root query part that contains or builds the complete query search condition
   */
  IQueryPart getRootQueryPart();

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
  IQueryCondition near(String field, double x, double y, int maxDistance);

  /**
   * Create a query condition for the {@link QueryOperator#CONTAINS} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that must be contained
   * @return
   */
  IQueryCondition contains(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#ENDS} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must end with
   * @return
   */
  IQueryCondition endsWith(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#STARTS} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must start with
   * @return
   */
  IQueryCondition startsWith(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   * 
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  IQueryCondition notIn(String field, Collection<?> values);

  /**
   * Create a query condition for the {@link QueryOperator#NOT_IN} operator
   * 
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  IQueryCondition notIn(String field, Object... values);

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
  IQueryCondition in(String field, Collection<?> values);

  /**
   * Create a query condition for the {@link QueryOperator#IN} operator
   * 
   * @param field
   *          the field for the comparison
   * @param values
   *          the values for the comparison
   * @return
   */
  IQueryCondition in(String field, Object... values);

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER_EQUAL} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be smaller or equal to
   * @return
   */
  IQueryCondition smallerOrEqual(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#SMALLER} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be smaller than
   * @return
   */
  IQueryCondition smaller(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#LARGER_EQUAL} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be larger or equal to
   * @return
   */
  IQueryCondition largerOrEqual(String field, Object value);

  /**
   * Create a query condition for the {@link QueryOperator#LARGER} operator
   * 
   * @param field
   *          the field for the comparison
   * @param value
   *          the value that the record must be larger than
   * @return
   */
  IQueryCondition larger(String field, Object value);

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
  IQueryCondition notEqual(String field, Object value);

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
  IQueryCondition isEqual(String field, Object value);

  /**
   * Connects the given query parts with the {@link QueryLogic#OR} connector
   * 
   * @param queryParts
   *          the query parts to connect
   * @return
   */
  IQueryContainer or(IQueryPart... queryParts);

  /**
   * Connects the given query parts with the {@link QueryLogic#AND} connector
   * 
   * @param queryParts
   *          the query parts to connect
   * @return
   */
  IQueryContainer and(IQueryPart... queryParts);

  /**
   * Build the complete query expression that contains all info needed to execute this query against the current
   * datastore
   * 
   * @param resultHandler
   *          handler for the completed {@link IQueryExpression}
   */
  public void buildQueryExpression(Handler<AsyncResult<IQueryExpression>> resultHandler);
}
