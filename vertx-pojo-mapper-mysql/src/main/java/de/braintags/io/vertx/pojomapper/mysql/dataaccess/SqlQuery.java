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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.SqlUtil;
import de.braintags.io.vertx.pojomapper.mysql.exception.SqlException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.ResultSet;

/**
 * 
 * An implementation of {@link IQuery} for sql databases
 * 
 * @param <T>
 *          the type of the mapper, which is handled here
 * @author Michael Remme
 * 
 */

public class SqlQuery<T> extends Query<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlQuery.class);

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlQuery(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
   */
  @Override
  public void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          createQueryDefinition(result -> {
            if (result.failed()) {
              resultHandler.handle(Future.failedFuture(result.cause()));
              return;
            }
            doFind(result.result(), resultHandler);
          });
        } catch (Exception e) {
          LOGGER.debug("error occured", e);
          Future<IQueryResult<T>> future = Future.failedFuture(e);
          resultHandler.handle(future);
        }
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeCount(io.vertx.core.Handler)
   */
  @Override
  public void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          createQueryDefinition(result -> {
            if (result.failed()) {
              resultHandler.handle(Future.failedFuture(result.cause()));
              return;
            }
            executeCount(result.result(), resultHandler);
          });
        } catch (Exception e) {
          Future<IQueryCountResult> future = Future.failedFuture(e);
          resultHandler.handle(future);
        }
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
   */
  @Override
  public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not implemented yet")));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#parent()
   */
  @Override
  public Object parent() {
    return null;
  }

  /**
   * Create the statement which will be executed
   * 
   * @param resultHandler
   *          the resulthandler which will receive notification
   */
  void createQueryDefinition(Handler<AsyncResult<SqlQueryRambler>> resultHandler) {
    LOGGER.debug("create query definition");
    SqlQueryRambler rambler = new SqlQueryRambler();
    executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.debug("rambler failed", result.cause());
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        LOGGER.debug("rambler finished");
        resultHandler.handle(Future.succeededFuture(rambler));
      }
    });
  }

  private void executeCount(SqlQueryRambler query, Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    SqlExpression statement = (SqlExpression) query.getQueryExpression();
    if (statement.hasQueryParameters()) {
      SqlUtil.queryWithParams((MySqlDataStore) getDataStore(), statement.getCountExpression(),
          statement.getParameters(), qRes -> handleCountResult(qRes, query, resultHandler));
    } else {
      SqlUtil.query((MySqlDataStore) getDataStore(), statement.getCountExpression(),
          qRes -> handleCountResult(qRes, query, resultHandler));
    }
  }

  private void handleCountResult(AsyncResult<ResultSet> qRes, SqlQueryRambler query,
      Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    if (qRes.failed()) {
      String message = "Executed count: " + query.getQueryExpression().toString();
      resultHandler.handle(Future.failedFuture(new SqlException(message, qRes.cause())));
      return;
    }
    SqlQueryCountResult cr = new SqlQueryCountResult(getMapper(), getDataStore(), qRes.result(),
        query.getQueryExpression());
    resultHandler.handle(Future.succeededFuture(cr));
  }

  private void doFind(SqlQueryRambler query, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    LOGGER.debug("start doFind");
    SqlExpression statement = (SqlExpression) query.getQueryExpression();
    if (statement.hasQueryParameters()) {
      SqlUtil.queryWithParams((MySqlDataStore) getDataStore(), statement.getSelectExpression(),
          statement.getParameters(), qRes -> handleQueryResult(qRes, query, resultHandler));
    } else {
      SqlUtil.query((MySqlDataStore) getDataStore(), statement.getSelectExpression(),
          qRes -> handleQueryResult(qRes, query, resultHandler));
    }
  }

  private void handleQueryResult(AsyncResult<ResultSet> qRes, SqlQueryRambler query,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    if (qRes.failed()) {
      String message = "Executed query: " + query.getQueryExpression().toString();
      resultHandler.handle(Future.failedFuture(new SqlException(message, qRes.cause())));
      return;
    }
    SqlQueryResult<T> qr = createQueryResult(qRes.result(), query);
    resultHandler.handle(Future.succeededFuture(qr));

  }

  private SqlQueryResult<T> createQueryResult(ResultSet resultSet, SqlQueryRambler query) {
    return new SqlQueryResult<T>(resultSet, (MySqlDataStore) getDataStore(), getMapper(), query);
  }
}
