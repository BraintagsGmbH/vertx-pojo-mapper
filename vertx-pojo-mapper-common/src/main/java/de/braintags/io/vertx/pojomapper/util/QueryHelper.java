/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.util;

import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.util.IteratorAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * A helper class with several static methods to simplyfy search actions
 * 
 * @author Michael Remme
 * 
 */
public class QueryHelper {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(QueryHelper.class);

  private QueryHelper() {
  }

  /**
   * Performs a query by id and returns the found instance, or null, if none
   * 
   * @param datastore
   *          the datastore to be used
   * @param mapperClass
   *          the mapper class
   * @param id
   *          the id to search for
   * @param handler
   *          the handler to be informed
   */
  public static final void findRecordById(IDataStore datastore, Class<?> mapperClass, String id,
      Handler<AsyncResult<?>> handler) {
    IQuery<?> query = datastore.createQuery(mapperClass);
    query.field(query.getMapper().getIdField().getName()).is(id);
    executeToFirstRecord(query, handler);
  }

  /**
   * Executes the given {@link IQuery} and returns teh first record directly to the handler. This method can be used,
   * when only one record is expected to be found, like an ID query, for instance
   * 
   * @param query
   *          the query to be executed
   * @param handler
   *          the handler, which will receive the first object
   */
  public static void executeToFirstRecord(IQuery<?> query, Handler<AsyncResult<?>> handler) {
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IteratorAsync<?> it = result.result().iterator();
        if (it.hasNext()) {
          it.next(itResult -> {
            if (itResult.failed()) {
              handler.handle(Future.failedFuture(itResult.cause()));
            } else {
              handler.handle(Future.succeededFuture(itResult.result()));
            }
          });
        } else {
          handler.handle(Future.succeededFuture(null));
        }
      }
    });
  }

  /**
   * Executes the given {@link IQuery} and returns all found records as {@link List} directly to
   * the handler
   * 
   * @param query
   *          the query to be executed
   * @param handler
   *          the handler, which will receive the list of objects
   */
  public static void executeToList(IQuery<?> query, Handler<AsyncResult<List<?>>> handler) {
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        LOGGER.info("executed query: " + result.result().toString());
        queryResultToList(result.result(), handler);
      }
    });
  }

  /**
   * Creates a complete {@link List} of objects from the given {@link IQueryResult}
   * 
   * @param queryResult
   *          the {@link IQueryResult} to be handled
   * @param handler
   *          the handler to be informed
   */
  public static final void queryResultToList(IQueryResult<?> queryResult, Handler<AsyncResult<List<?>>> handler) {
    queryResult.toArray(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(Arrays.asList(result.result())));
      }
    });
  }

}
