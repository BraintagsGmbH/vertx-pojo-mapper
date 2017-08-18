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

import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
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
   * Execute the query with the default limit of the current datastore. Any variables in the search condition will
   * result in an error. For queries with variables and custom limit, see
   * {@link #execute(IFieldValueResolver, int, int, Handler)}
   *
   * @param resultHandler
   */
  void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Execute the query
   *
   * @param resolver
   *          replaces potential variables in the search condition with an actual value, can be null
   * @param limit
   *          the maximum number of results to search
   * @param offset
   *          the offset of the first row to return
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  void execute(IFieldValueResolver resolver, int limit, int offset,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Execute the query by counting the fitting objects. Any variables in the search condition will result in an error.
   * For queries with variables, see {@link #executeCount(IFieldValueResolver, Handler)}
   *
   * @param resultHandler
   */
  void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler);

  /**
   * Execute the query by counting the fitting objects
   *
   * @param resolver
   *          replaces potential variables in the search condition with an actual value, can be null
   * @param resultHandler
   *          contains the {@link IQueryCountResult}
   */
  void executeCount(IFieldValueResolver resolver, Handler<AsyncResult<IQueryCountResult>> resultHandler);

  /**
   * Execute the query with the option explain and sends back the suitable information
   *
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Build the query expression that contains the info needed to execute this query against the current
   * datastore
   *
   * @param resolver
   *          replaces potential variables in the search condition with an actual value, can be null
   * @param resultHandler
   *          handler for the completed {@link IQueryExpression}
   */
  void buildQueryExpression(IFieldValueResolver resolver, Handler<AsyncResult<IQueryExpression>> resultHandler);

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
   * @param searchCondition
   *          the root condition that contains or builds the complete query search condition
   */
  void setSearchCondition(ISearchCondition searchCondition);

  /**
   * Get the complete search condition of the query
   *
   * @return the root condition that contains or builds the complete query search condition
   */
  ISearchCondition getSearchCondition();

  /**
   * Add a use field to the query, restricting the result of the query to only the values of fields that were added
   * 
   * @param fieldName
   *          the field to add to the result output
   */
  void addUseField(String fieldName);

  /**
   * Set the use fields that restrict the result of the query
   * 
   * @param useFields
   *          the use fields to set
   */
  void setUseFields(List<String> useFields);

  /**
   * Get the use fields that restrict the result of the query
   * 
   * @return the use fields
   */
  List<String> getUseFields();

}
