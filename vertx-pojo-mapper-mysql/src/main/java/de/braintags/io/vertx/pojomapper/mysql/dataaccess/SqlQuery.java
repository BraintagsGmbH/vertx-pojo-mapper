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
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlQuery<T> extends Query<T> {

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
        throw new UnsupportedOperationException();

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
    SqlQueryRambler rambler = new SqlQueryRambler();
    executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(rambler));
      }
    });
  }

  private void doFind(SqlQueryRambler query, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    ((MySqlDataStore) getDataStore()).getSqlClient().getConnection(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
        return;
      }
      SQLConnection connection = cr.result();
      executeQuery(connection, query, resultHandler);
    });
  }

  private void executeQuery(SQLConnection connection, SqlQueryRambler query,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    if (query.hasQueryParameters()) {
      connection.queryWithParams(query.getQueryStatement(), query.getQueryParameters(),
          qRes -> handleQueryResult(qRes, connection, query, resultHandler));
    } else {
      connection.query(query.getQueryStatement(), qRes -> handleQueryResult(qRes, connection, query, resultHandler));
    }
  }

  private void handleQueryResult(AsyncResult<ResultSet> qRes, SQLConnection connection, SqlQueryRambler query,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    connection.close();
    if (qRes.failed()) {
      resultHandler.handle(Future.failedFuture(qRes.cause()));
      return;
    }
    SqlQueryResult<T> qr = createQueryResult(qRes.result(), query);
    resultHandler.handle(Future.succeededFuture(qr));

  }

  private SqlQueryResult<T> createQueryResult(ResultSet resultSet, SqlQueryRambler query) {
    return new SqlQueryResult<T>(resultSet, (MySqlDataStore) getDataStore(), getMapper(), query);
  }
}
