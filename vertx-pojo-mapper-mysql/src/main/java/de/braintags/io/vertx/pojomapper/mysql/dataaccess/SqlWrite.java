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
import java.util.Objects;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.exception.DuplicateKeyException;
import de.braintags.io.vertx.pojomapper.exception.InsertException;
import de.braintags.io.vertx.pojomapper.exception.WriteException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.SqlUtil;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObject.SqlSequence;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
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
  private static final String LAST_INSERT_ID_COMMAND = "SELECT LAST_INSERT_ID();";
  private int saveSize;

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlWrite(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void internalSave(Handler<AsyncResult<IWriteResult>> resultHandler) {
    saveSize = getObjectsToSave().size();
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(new SqlWriteResult()));
      return;
    }
    getDataStore().getMapperFactory().getStoreObjectFactory().createStoreObjects(getMapper(), getObjectsToSave(),
        stoResult -> {
          if (stoResult.failed()) {
            resultHandler.handle(Future.failedFuture(stoResult.cause()));
            return;
          } else {
            if (stoResult.result().size() != saveSize) {
              String message = String.format("Wrong number of StoreObjects created. Expected %d - created: %d",
                  saveSize, stoResult.result().size());
              LOGGER.error(message);
            }
            save(stoResult.result(), resultHandler);
          }
        });

  }

  private void save(List<IStoreObject<T, ? >> storeObjects, Handler<AsyncResult<IWriteResult>> resultHandler) {
    CounterObject<IWriteResult> co = new CounterObject<>(storeObjects.size(), resultHandler);
    WriteResult rr = new SqlWriteResult();
    for (IStoreObject<T, ? > sto : storeObjects) {
      saveStoreObject((SqlStoreObject<T>) sto, rr, saveResult -> {
        if (saveResult.failed()) {
          LOGGER.error("", saveResult.cause());
          co.setThrowable(saveResult.cause());
        } else if (co.reduce()) {
          if (rr.size() != saveSize) {
            String message = String.format("Wrong number of saved instances in WriteResult. Expected %d - created: %d",
                saveSize, rr.size());
            LOGGER.error(message);
          }
          resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (co.isError()) {
        break;
      }
    }
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void saveStoreObject(SqlStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    Object currentId = storeObject.get(getMapper().getIdField());
    if (currentId == null || (currentId instanceof Number && ((Number) currentId).intValue() == 0)) {
      handleInsert(storeObject, writeResult, resultHandler);
    } else {
      handleUpdate(storeObject, writeResult, resultHandler);
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
  private void handleUpdate(SqlStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlUpdateStatement();
    if (seq.getParameters().isEmpty()) {
      SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(), updateResult -> {
        checkUpdateResult(seq, updateResult, checkResult -> {
          if (checkResult.failed()) {
            resultHandler.handle(checkResult);
          } else {
            finishUpdate(storeObject, writeResult, resultHandler);
          }
        });
      });
    } else {
      SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
          updateResult -> {
            checkUpdateResult(seq, updateResult, checkResult -> {
              if (checkResult.failed()) {
                resultHandler.handle(checkResult);
              } else {
                finishUpdate(storeObject, writeResult, resultHandler);
              }
            });
          });
    }

  }

  @SuppressWarnings("unchecked")
  private void finishUpdate(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    Object id = getMapper().getIdField().getPropertyAccessor().readData(storeObject.getEntity());
    LOGGER.debug("updated record with id " + id);
    try {
      executePostSave((T) storeObject.getEntity(), lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(lcr);
        } else {
          writeResult.addEntry(storeObject, id, WriteAction.UPDATE);
          resultHandler.handle(Future.succeededFuture());
        }
      });
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  private void handleInsert(SqlStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    storeObject.generateSqlInsertStatement(isr -> {
      if (isr.failed()) {
        resultHandler.handle(Future.failedFuture(isr.cause()));
      } else {
        SqlSequence seq = isr.result();
        if (seq.getParameters().isEmpty()) {
          insertWithoutParameters(storeObject, writeResult, seq, resultHandler);
        } else {
          insertWithParameters(storeObject, writeResult, seq, resultHandler);
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
  private void insertWithoutParameters(SqlStoreObject storeObject, IWriteResult writeResult, SqlSequence seq,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(), updateResult -> {
      checkUpdateResult(seq, updateResult, checkResult -> {
        if (checkResult.failed()) {
          resultHandler.handle(checkResult);
        } else {
          finishInsert(storeObject, writeResult, resultHandler);
        }
      });
    });
  }

  private void insertWithParameters(SqlStoreObject storeObject, IWriteResult writeResult, SqlSequence seq,
      Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
        updateResult -> {
          checkUpdateResult(seq, updateResult, checkResult -> {
            if (checkResult.failed()) {
              handleInsertError(checkResult.cause(), storeObject, writeResult, resultHandler);
            } else {
              finishInsert(storeObject, writeResult, resultHandler);
            }
          });
        });
  }

  private void handleInsertError(Throwable t, SqlStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    if (t instanceof DuplicateKeyException) {
      if (getMapper().getKeyGenerator() != null) {
        LOGGER.info("duplicate key, regenerating a new key");
        storeObject.generateSqlInsertStatement(niResult -> {
          if (niResult.failed()) {
            resultHandler.handle(Future.failedFuture(
                new WriteException("Could not generate new ID after duplicate key error", niResult.cause())));
          } else {
            insertWithParameters(storeObject, writeResult, niResult.result(), resultHandler);
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
  private void checkUpdateResult(SqlSequence seq, AsyncResult<UpdateResult> updateResult,
      Handler<AsyncResult<Void>> resultHandler) {
    if (updateResult.failed()) {
      Exception we = updateResult.cause() instanceof DuplicateKeyException
          ? (DuplicateKeyException) updateResult.cause() : new WriteException(updateResult.cause());
      resultHandler.handle(Future.failedFuture(we));
    } else {
      UpdateResult res = updateResult.result();
      if (res.getUpdated() != 1) {
        String message = String.format(
            "Error inserting a record, expected %d records saved, but was %d With sequence %s", 1, res.getUpdated(),
            seq);
        resultHandler.handle(Future.failedFuture(new InsertException(message)));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void finishInsert(SqlStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    try {
      Object id = storeObject.get(getMapper().getIdField());
      Objects.requireNonNull(id, "Undefined ID when storing record");
      LOGGER.debug("==>>>> inserted record " + storeObject.getMapper().getTableInfo().getName() + " with id " + id);
      executePostSave((T) storeObject.getEntity(), lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(lcr);
        } else {
          setIdValue(id, storeObject, result -> {
            if (result.failed()) {
              resultHandler.handle(result);
            } else {
              writeResult.addEntry(storeObject, id, WriteAction.INSERT);
              resultHandler.handle(Future.succeededFuture());
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
  protected void setIdValue(Object id, IStoreObject<T, ? > storeObject, Handler<AsyncResult<Void>> resultHandler) {
    IField idField = getMapper().getIdField();
    idField.getPropertyMapper().fromStoreObject(storeObject.getEntity(), storeObject, idField, resultHandler);
  }

}
