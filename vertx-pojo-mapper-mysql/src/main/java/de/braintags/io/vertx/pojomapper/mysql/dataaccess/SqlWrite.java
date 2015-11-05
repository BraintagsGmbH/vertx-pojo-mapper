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
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.exception.InsertException;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.SqlUtil;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObject.SqlSequence;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.UpdateResult;

/**
 * 
 * An implementation of {@link IWrite} for sql databases
 * 
 * @param <T>
 *          the type of the mapper, which is handled here
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
    if (currentId == null || (currentId instanceof Number && ((Number) currentId).intValue() == 0)) {
      handleInsert(storeObject, writeResult, ir -> {
        resultHandler.handle(ir);
      });
    } else {
      handleUpdate(storeObject, writeResult, ir -> {
        resultHandler.handle(ir);
      });
    }

  }

  /**
   * Perform an update of a record into the datastore
   * 
   * @param storeObject
   *          the {@link IStoreObject}
   * @param writeResult
   *          the {@link IWriteResult} to be filled
   * @param resultHandler
   *          the {@link Handler} to be informed
   */
  private void handleUpdate(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlUpdateStatement();
    if (seq.getParameters().isEmpty()) {
      // should not happen, but ...
      resultHandler
          .handle(Future.failedFuture("Update without parameters should not happen normally: " + seq.toString()));
    } else {
      SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
          updateResult -> {
            if (!checkUpdateResult(seq, updateResult, resultHandler))
              return;
            finishUpdate(storeObject, writeResult, resultHandler);
          });
    }

  }

  @SuppressWarnings("unchecked")
  private void finishUpdate(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    Object id = getMapper().getIdField().getPropertyAccessor().readData(storeObject.getEntity());
    LOGGER.debug("updated record with id " + id);
    try {
      executePostSave((T) storeObject.getEntity());
      writeResult.addEntry(storeObject, id, WriteAction.UPDATE);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  private void handleInsert(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlInsertStatement();
    if (seq.getParameters().isEmpty()) {
      insertWithoutParameters(storeObject, writeResult, seq, resultHandler);
    } else {
      insertWithParameters(storeObject, writeResult, seq, resultHandler);
    }
  }

  /**
   * This can happen, when a record is inserted which has only the ID field
   * 
   * @param storeObject
   * @param writeResult
   * @param connection
   * @param seq
   * @param resultHandler
   */
  private void insertWithoutParameters(SqlStoreObject storeObject, IWriteResult writeResult, SqlSequence seq,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(), updateResult -> {
      if (!checkUpdateResult(seq, updateResult, resultHandler))
        return;
      UpdateResult res = updateResult.result();
      if (res.getKeys() != null && res.getKeys().size() == 1) {
        finishInsert(storeObject, writeResult, res.getKeys().getValue(0), resultHandler);
        return;
      }
      loadLastInsertedId(storeObject, writeResult, resultHandler);
    });
  }

  private void insertWithParameters(SqlStoreObject storeObject, IWriteResult writeResult, SqlSequence seq,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
        updateResult -> {
          if (!checkUpdateResult(seq, updateResult, resultHandler))
            return;
          UpdateResult res = updateResult.result();
          if (res.getKeys() != null && res.getKeys().size() == 1) {
            finishInsert(storeObject, writeResult, res.getKeys().getValue(0), resultHandler);
            return;
          }
          loadLastInsertedId(storeObject, writeResult, resultHandler);
        });
  }

  private void loadLastInsertedId(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.query((MySqlDataStore) getDataStore(), LAST_INSERT_ID_COMMAND, idResult -> {
      if (idResult.failed()) {
        resultHandler.handle(Future.failedFuture(idResult.cause()));
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
  }

  /**
   * Checks the UpdateResult and informs the Handler with an error, if needed
   * 
   * @param updateResult
   *          the result to be checked
   * @param resultHandler
   *          the {@link Handler} to be informed
   * @return false, if an error occured and handler was sent an error, otherwise true
   */
  private boolean checkUpdateResult(SqlSequence seq, AsyncResult<UpdateResult> updateResult,
      Handler<AsyncResult<Void>> resultHandler) {
    if (updateResult.failed()) {
      resultHandler.handle(Future.failedFuture(updateResult.cause()));
      return false;
    }
    UpdateResult res = updateResult.result();
    if (res.getUpdated() != 1) {
      String message = String.format("Error inserting a record, expected %d records saved, but was %d", 1,
          res.getUpdated());
      resultHandler.handle(Future.failedFuture(new InsertException(message)));
      return false;
    }
    return true;

  }

  @SuppressWarnings("unchecked")
  private void finishInsert(SqlStoreObject storeObject, IWriteResult writeResult, Object id,
      Handler<AsyncResult<Void>> resultHandler) {
    LOGGER.debug("inserted record with id " + id);
    try {
      executePostSave((T) storeObject.getEntity());
      setIdValue(id, storeObject, result -> {
        if (result.failed()) {
          resultHandler.handle(result);
          return;
        }
        writeResult.addEntry(storeObject, id, WriteAction.INSERT);
        resultHandler.handle(Future.succeededFuture());
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      resultHandler.handle(Future.failedFuture(e));
    }
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
