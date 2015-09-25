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
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLConnection;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlWrite<T> extends AbstractWrite<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqlWrite.class);

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
      } else {
        ((MySqlDataStore) getDataStore()).getSqlClient().getConnection(cr -> doSave(cr, resultHandler));
      }
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
      try {
        ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>();
        CounterObject counter = new CounterObject(getObjectsToSave().size());
        for (T entity : getObjectsToSave()) {
          saveEntity(entity, rr, connection, result -> {
            if (result.failed()) {
              ro.setThrowable(result.cause());
            } else {
              if (counter.reduce()) {
                resultHandler.handle(Future.succeededFuture(rr));
                return;
              }
            }
          });
          if (ro.handleError(resultHandler))
            return;
        }

      } finally {
        LOGGER.debug("closing connection - sync finished");
        connection.close();
      }
    }
  }

  private void saveEntity(T entity, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        doSaveEntity(entity, (JsonStoreObject) result.result(), writeResult, connection, sResult -> {
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
  private void doSaveEntity(T entity, JsonStoreObject storeObject, IWriteResult writeResult, SQLConnection connection,
      Handler<AsyncResult<Void>> resultHandler) {
    IMapper mapper = getMapper();
    String tableName = mapper.getTableInfo().getName();
    Object currentId = storeObject.get(mapper.getIdField());
    LOGGER.info("now saving");

    /*
     * LAST_INSERT_ID The ID that was generated is maintained in the server on a per-connection basis. This means that
     * the value returned by the function to a given client is the first AUTO_INCREMENT value generated for most recent
     * statement affecting an AUTO_INCREMENT column by that client. This value cannot be affected by other clients, even
     * if they generate AUTO_INCREMENT values of their own. This behavior ensures that each client can retrieve its own
     * ID without concern for the activity of other clients, and without the need for locks or transactions.
     * 
     * 
     */

    if (currentId == null) {
      // INSERT INTO tbl_name VALUES (1, "row 1"), (2, "row 2");

    } else {

    }

    // connection.updateWithParams(arg0, arg1, arg2);
    resultHandler.handle(Future.failedFuture(new UnsupportedOperationException()));

    // mongoClient.save(column, storeObject.getContainer(), result -> {
    // if (result.failed()) {
    // LOGGER.info("failed", result.cause());
    // Future<Void> future = Future.failedFuture(result.cause());
    // resultHandler.handle(future);
    // return;
    // } else {
    // LOGGER.info("saved");
    // WriteAction action = WriteAction.UNKNOWN;
    // String id = result.result();
    // if (id == null) {
    // id = currentId;
    // action = WriteAction.UPDATE;
    // } else
    // action = WriteAction.INSERT;
    // executePostSave(entity);
    // writeResult.addEntry(storeObject, id, action);
    // resultHandler.handle(Future.succeededFuture());
    // }
    // });

  }

}
