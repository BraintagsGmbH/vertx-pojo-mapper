/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mysql.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteEntry;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mysql.MySqlDataStore;
import de.braintags.vertx.jomnigate.mysql.SqlUtil;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlStoreObject.SqlSequence;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlWrite(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void internalSave(Handler<AsyncResult<IWriteResult>> resultHandler) {
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(new SqlWriteResult()));
    } else {
      getDataStore().getMapperFactory().getStoreObjectFactory().createStoreObjects(getMapper(), getObjectsToSave(),
          stoResult -> {
            if (stoResult.failed()) {
              resultHandler.handle(Future.failedFuture(stoResult.cause()));
            } else {
              CompositeFuture cf = saveRecords(stoResult.result());
              cf.setHandler(cfr -> {
                if (cfr.failed()) {
                  resultHandler.handle(Future.failedFuture(cfr.cause()));
                } else {
                  resultHandler.handle(Future.succeededFuture(new SqlWriteResult(cf.list())));
                }
              });
            }
          });

    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private CompositeFuture saveRecords(List<IStoreObject<T, ?>> storeObjects) {
    List<Future> fl = new ArrayList<>(storeObjects.size());
    for (IStoreObject<T, ?> storeObject : storeObjects) {
      fl.add(saveStoreObject((SqlStoreObject<T>) storeObject));
    }
    return CompositeFuture.all(fl);
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private Future saveStoreObject(SqlStoreObject<T> storeObject) {
    Future<IWriteEntry> f = Future.future();
    Object currentId = storeObject.get(getMapper().getIdField().getField());
    if (currentId == null || (currentId instanceof Number && ((Number) currentId).intValue() == 0)) {
      handleInsert(storeObject, f.completer());
    } else {
      handleUpdate(storeObject, f.completer());
    }
    return f;
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
  @SuppressWarnings("rawtypes")
  private void handleUpdate(SqlStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlUpdateStatement();
    if (seq.getParameters().isEmpty()) {
      SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(),
          updateResult -> checkUpdateResult(updateResult, checkResult -> {
            if (checkResult.failed()) {
              resultHandler.handle(Future.failedFuture(checkResult.cause()));
            } else {
              finishUpdate(storeObject, resultHandler);
            }
          }));
    } else {
      SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
          updateResult -> checkUpdateResult(updateResult, checkResult -> {
            if (checkResult.failed()) {
              resultHandler.handle(Future.failedFuture(checkResult.cause()));
            } else {
              finishUpdate(storeObject, resultHandler);
            }
          }));
    }

  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void finishUpdate(SqlStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    Object id = getMapper().getIdField().getField().getPropertyAccessor().readData(storeObject.getEntity());
    LOGGER.debug("updated record with id " + id);
    try {
      executePostSave(storeObject.getEntity(), lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(Future.failedFuture(lcr.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
        }
      });
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @SuppressWarnings("rawtypes")
  private void handleInsert(SqlStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    storeObject.generateSqlInsertStatement(isr -> {
      if (isr.failed()) {
        resultHandler.handle(Future.failedFuture(isr.cause()));
      } else {
        SqlSequence seq = isr.result();
        if (seq.getParameters().isEmpty()) {
          insertWithoutParameters(storeObject, seq, resultHandler);
        } else {
          insertWithParameters(storeObject, seq, resultHandler);
        }
      }
    });
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
  @SuppressWarnings("rawtypes")
  private void insertWithoutParameters(SqlStoreObject<T> storeObject, SqlSequence seq,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(),
        updateResult -> checkUpdateResult(updateResult, checkResult -> {
          if (checkResult.failed()) {
            resultHandler.handle(Future.failedFuture(checkResult.cause()));
          } else {
            finishInsert(storeObject, resultHandler);
          }
        }));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void insertWithParameters(SqlStoreObject<T> storeObject, SqlSequence seq,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
        updateResult -> checkUpdateResult(updateResult, checkResult -> {
          if (checkResult.failed()) {
            handleInsertError(checkResult.cause(), storeObject, resultHandler);
          } else {
            finishInsert(storeObject, resultHandler);
          }
        }));
  }

  private void handleInsertError(Throwable t, SqlStoreObject<T> storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (t instanceof DuplicateKeyException) {
      if (getMapper().getKeyGenerator() != null) {
        LOGGER.info("duplicate key, regenerating a new key");
        storeObject.generateSqlInsertStatement(niResult -> {
          if (niResult.failed()) {
            resultHandler.handle(Future.failedFuture(
                new WriteException("Could not generate new ID after duplicate key error", niResult.cause())));
          } else {
            insertWithParameters(storeObject, niResult.result(), resultHandler);
          }
        });
      } else {
        resultHandler.handle(Future
            .failedFuture(new WriteException("Duplicate key error on insert, but no KeyGenerator is defined", t)));
      }
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException(t)));
    }
  }

  /**
   * Checks the UpdateResult and informs the Handler with an error, if needed
   * 
   * @param updateResult
   *          the result to be checked
   * @param resultHandler
   *          the {@link Handler} to be informed
   */
  private void checkUpdateResult(AsyncResult<UpdateResult> updateResult, Handler<AsyncResult<Void>> resultHandler) {
    if (updateResult.failed()) {
      Exception we = updateResult.cause() instanceof DuplicateKeyException
          ? (DuplicateKeyException) updateResult.cause() : new WriteException(updateResult.cause());
      resultHandler.handle(Future.failedFuture(we));
    } else {
      resultHandler.handle(Future.succeededFuture());
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void finishInsert(SqlStoreObject storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    try {
      Object id = storeObject.get(getMapper().getIdField().getField());
      Objects.requireNonNull(id, "Undefined ID when storing record");
      LOGGER.debug("==>>>> inserted record " + storeObject.getMapper().getTableInfo().getName() + " with id " + id);
      executePostSave((T) storeObject.getEntity(), lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(Future.failedFuture(lcr.cause()));
        } else {
          setIdValue(id, storeObject, result -> {
            if (result.failed()) {
              resultHandler.handle(Future.failedFuture(result.cause()));
            } else {
              resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.INSERT)));
            }
          });
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  /**
   * With mysql the id is generated before, so there is no need to use the super method, where the id is filled into the
   * {@link IStoreObject}
   */
  @Override
  protected void setIdValue(Object id, IStoreObject<T, ?> storeObject, Handler<AsyncResult<Void>> resultHandler) {
    IField idField = getMapper().getIdField().getField();
    idField.getPropertyMapper().fromStoreObject(storeObject.getEntity(), storeObject, idField, resultHandler);
  }

}
