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

import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import edu.emory.mathcs.backport.java.util.Arrays;
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

  private QueryHelper() {
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
