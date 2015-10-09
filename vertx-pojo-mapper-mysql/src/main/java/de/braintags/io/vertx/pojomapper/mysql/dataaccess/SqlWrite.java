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
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
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
      if (getObjectsToSave().isEmpty()) {
        resultHandler.handle(Future.succeededFuture(new WriteResult()));
        return;
      }
      getDataStore().getStoreObjectFactory().createStoreObjects(getMapper(), getObjectsToSave(), stoResult -> {
        if (stoResult.failed()) {
          resultHandler.handle(Future.failedFuture(stoResult.cause()));
          return;
        }
        save(stoResult.result(), resultHandler);
      });

    });
  }

  private void save(List<IStoreObject<?>> storeObjects, Handler<AsyncResult<IWriteResult>> resultHandler) {
    CounterObject co = new CounterObject(storeObjects.size());
    ErrorObject<IWriteResult> err = new ErrorObject<>(resultHandler);
    WriteResult rr = new WriteResult();
    for (IStoreObject<?> sto : storeObjects) {
      saveStoreObject((SqlStoreObject) sto, rr, saveResult -> {
        if (saveResult.failed()) {
          err.setThrowable(saveResult.cause());
          return;
        }

        if (co.reduce()) {
          resultHandler.handle(Future.succeededFuture(rr));
          return;
        }

      });
      if (err.isError())
        return;
    }
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void saveStoreObject(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    Object currentId = storeObject.get(getMapper().getIdField());
    ((MySqlDataStore) getDataStore()).getSqlClient().getConnection(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
        return;
      }
      SQLConnection connection = cr.result();
      if (currentId == null) {
        handleInsert(storeObject, writeResult, connection, ir -> {
          closeConnection(connection);
          resultHandler.handle(ir);
        });
      } else {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException()));

      }

    });

  }

  private void closeConnection(SQLConnection connection) {
    try {
      LOGGER.debug("closing connection - save finished");
      connection.close();
    } catch (Exception e) {
      LOGGER.warn("Error in closing connection", e);
    }
  }

  private void handleInsert(SqlStoreObject storeObject, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlInsertStatement();
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
    LOGGER.debug("inserted record with id " + id);
    executePostSave((T) storeObject.getEntity());
    setIdValue(id, storeObject, resultHandler);
    writeResult.addEntry(storeObject, id, WriteAction.INSERT);
    resultHandler.handle(Future.succeededFuture());
  }

  /**
   * After inserting an instance, the id is placed into the entity. NOTE: this is still a hack, cause we are expecting
   * the id field to be a String or numeric field, because of a potential switch from Mongo to MySql. Normally this
   * should be handled by a special ITypeHandler, which can deal with changing types
   * 
   * @param id
   * @param storeObject
   */
  private void setIdValue(Object id, SqlStoreObject storeObject, Handler<AsyncResult<Void>> resultHandler) {
    IField idField = getMapper().getIdField();
    Class fieldClass = idField.getType();
    if (fieldClass.equals(Long.class)) {
      if (id instanceof String)
        id = Long.parseLong((String) id);
    } else if (fieldClass.equals(String.class)) {
      if (id instanceof Long)
        id = String.valueOf(id);
    } else
      resultHandler.handle(Future
          .failedFuture(new UnsupportedOperationException("unsupported type for id field: " + fieldClass.getName())));
    idField.getPropertyAccessor().writeData(storeObject.getEntity(), id);
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
