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

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.exception.InsertException;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObject.SqlSequence;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlWrite<T> extends AbstractWrite<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqlWrite.class);
  private static final String LAST_INSERT_ID_COMMAND = "SELECT LAST_INSERT_ID();";

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlWrite(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
        return;
      }

      ((MySqlDataStore) getDataStore()).getSqlClient().getConnection(cr -> doSave(cr, resultHandler));

    });
  }

  private void doSave(AsyncResult<SQLConnection> cr, Handler<AsyncResult<IWriteResult>> resultHandler) {
    if (cr.failed()) {
      resultHandler.handle(Future.failedFuture(cr.cause()));
      return;
    } else {
      WriteResult rr = new WriteResult();
      if (getObjectsToSave().isEmpty()) {
        resultHandler.handle(Future.succeededFuture(rr));
        return;
      }
      SQLConnection connection = cr.result();

      ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>(resultHandler);
      CounterObject counter = new CounterObject(getObjectsToSave().size());
      LOGGER.info(String.format("saving %d entities", getObjectsToSave().size()));
      for (T entity : getObjectsToSave()) {
        LOGGER.info("saving entity");
        saveEntity(entity, rr, connection, result -> {
          if (result.failed()) {
            ro.setThrowable(result.cause());
            closeConnection(connection);
          } else {
            if (counter.reduce()) {
              resultHandler.handle(Future.succeededFuture(rr));
              closeConnection(connection);
              return;
            }
          }
        });
        if (ro.isError()) {
          return;
        }
      }
    }
  }

  private void closeConnection(SQLConnection connection) {
    try {
      LOGGER.debug("closing connection - save finished");
      connection.close();
    } catch (Exception e) {
      LOGGER.warn("Error in closing connection", e);
    }
  }

  private void saveEntity(T entity, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        doSaveEntity(entity, (SqlStoreObject) result.result(), writeResult, connection, sResult -> {
          if (sResult.failed()) {
            resultHandler.handle(Future.failedFuture(sResult.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSaveEntity(T entity, SqlStoreObject storeObject, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    Object currentId = storeObject.get(getMapper().getIdField());
    LOGGER.info("now saving");

    if (currentId == null) {
      handleInsert(storeObject, writeResult, connection, resultHandler);
    } else {
      resultHandler.handle(Future.failedFuture(new UnsupportedOperationException()));

    }
  }

  private void handleInsert(SqlStoreObject storeObject, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlInsertStatement();
    LOGGER.info("inserting: " + storeObject.toString());
    connection.updateWithParams(seq.getSqlStatement(), seq.getParameters(), updateResult -> {
      if (updateResult.failed()) {
        resultHandler.handle(Future.failedFuture(updateResult.cause()));
        return;
      }
      UpdateResult res = updateResult.result();
      if (res.getUpdated() != 1) {
        String message = String.format("Error inserting a record, expected %d records saved, but was %d", 1,
            res.getUpdated());
        resultHandler.handle(Future.failedFuture(new InsertException(message)));
        return;
      }

      if (res.getKeys() != null && res.getKeys().size() == 1) {
        finishInsert(storeObject, writeResult, res.getKeys().getValue(0), resultHandler);
        return;
      }

      connection.query(LAST_INSERT_ID_COMMAND, idResult -> {
        if (idResult.failed()) {
          resultHandler.handle(Future.failedFuture(updateResult.cause()));
          return;
        }

        List<JsonObject> ids = idResult.result().getRows();
        if (ids.size() != 1) {
          resultHandler.handle(Future.failedFuture(new InsertException("Error reading last inserted id")));
          return;
        }
        finishInsert(storeObject, writeResult, ids.get(0).getValue("LAST_INSERT_ID()"), resultHandler);
        return;
      });
    });
  }

  private void finishInsert(SqlStoreObject storeObject, IWriteResult writeResult, Object id,
      Handler<AsyncResult<Void>> resultHandler) {
    LOGGER.info("inserted record with id " + id);
    executePostSave((T) storeObject.getEntity());
    writeResult.addEntry(storeObject, id, WriteAction.INSERT);
    resultHandler.handle(Future.succeededFuture());
  }

  /*
   * LAST_INSERT_ID The ID that was generated is maintained in the server on a per-connection basis. This means that the
   * value returned by the function to a given client is the first AUTO_INCREMENT value generated for most recent
   * statement affecting an AUTO_INCREMENT column by that client. This value cannot be affected by other clients, even
   * if they generate AUTO_INCREMENT values of their own. This behavior ensures that each client can retrieve its own ID
   * without concern for the activity of other clients, and without the need for locks or transactions.
   * 
   * 
   */

}
