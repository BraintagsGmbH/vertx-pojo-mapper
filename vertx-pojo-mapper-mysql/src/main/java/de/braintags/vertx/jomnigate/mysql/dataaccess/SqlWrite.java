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
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.AbstractWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteEntry;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mysql.MySqlDataStore;
import de.braintags.vertx.jomnigate.mysql.SqlUtil;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlStoreObject.SqlSequence;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
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
  public SqlWrite(final Class<T> mapperClass, final IDataStore<?, ?> datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public Future<IWriteResult> internalSave(final IObserverContext context) {
    if (partialUpdate) {
      throw new UnsupportedOperationException("Partial updates are not supported for sql right now");
    }
    Future<IWriteResult> f = Future.future();
    List<T> entities = getObjectsToSave();
    IMapper<T> mapper = getMapper();
    if (entities.isEmpty()) {
      f.complete(new SqlWriteResult());
    } else {
      ((SqlStoreObjectFactory) getDataStore().getStoreObjectFactory()).createStoreObjects(mapper, entities,
          stoResult -> {
            if (stoResult.failed()) {
              f.fail(stoResult.cause());
            } else {
              List<IStoreObject<T, Object>> storeObjects = stoResult.result();
              CompositeFuture cf = saveRecords(storeObjects);
              cf.setHandler(cfr -> {
                if (cfr.failed()) {
                  f.fail(cfr.cause());
                } else {
                  f.complete(new SqlWriteResult(cf.list()));
                }
              });
            }
          });
    }
    return f;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private CompositeFuture saveRecords(final List<IStoreObject<T, Object>> storeObjects) {
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
  private Future saveStoreObject(final SqlStoreObject<T> storeObject) {
    Future<IWriteEntry> f = Future.future();
    Object currentId = storeObject.get(getMapper().getIdInfo().getField());
    if (currentId == null || (currentId instanceof Number && ((Number) currentId).intValue() == 0)) {
      if (getQuery() != null) {
        throw new IllegalStateException("Can not update with a query and objects without id");
      }
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
  private void handleUpdate(final SqlStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlSequence seq = storeObject.generateSqlUpdateStatement();
    if (getQuery() != null) {
      if (getQuery().getNativeCommand() != null) {
        throw new IllegalStateException("Cann not update with a native command");
      }
      getQuery().buildQueryExpression(null, res -> {
        if (res.succeeded()) {
          seq.combineWhereClauses(((SqlExpression) res.result()));
          update(storeObject, resultHandler, seq);
        } else {
          resultHandler.handle(Future.failedFuture(res.cause()));
        }
      });
    } else {
      update(storeObject, resultHandler, seq);
    }
  }

  @SuppressWarnings("rawtypes")
  private void update(final SqlStoreObject<T> storeObject, final Handler<AsyncResult<IWriteEntry>> resultHandler,
      final SqlSequence seq) {
    if (seq.getParameters().isEmpty()) {
      SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(),
          updateResult -> checkUpdateResult(updateResult, checkResult -> {
            if (checkResult.failed()) {
              resultHandler.handle(Future.failedFuture(checkResult.cause()));
            } else {
              finishUpdate(storeObject, updateResult.result(), resultHandler);
            }
          }));
    } else {
      SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
          updateResult -> checkUpdateResult(updateResult, checkResult -> {
            if (checkResult.failed()) {
              resultHandler.handle(Future.failedFuture(checkResult.cause()));
            } else {
              finishUpdate(storeObject, updateResult.result(), resultHandler);
            }
          }));
    }
  }

  private void finishUpdate(final SqlStoreObject<T> storeObject, final UpdateResult updateResult,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    Object id = getMapper().getIdInfo().getField().getPropertyAccessor().readData(storeObject.getEntity());
    LOGGER.debug("updated record with id " + id);
    if (updateResult.getUpdated() == 0) {
      resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.NOT_MATCHED)));
    }
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
  private void handleInsert(final SqlStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
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
  private void insertWithoutParameters(final SqlStoreObject<T> storeObject, final SqlSequence seq,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlUtil.update((MySqlDataStore) getDataStore(), seq.getSqlStatement(),
        updateResult -> checkUpdateResult(updateResult, checkResult -> {
          if (checkResult.failed()) {
            resultHandler.handle(Future.failedFuture(checkResult.cause()));
          } else {
            finishInsert(storeObject, resultHandler);
          }
        }));
  }

  @SuppressWarnings({ "rawtypes" })
  private void insertWithParameters(final SqlStoreObject<T> storeObject, final SqlSequence seq,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(),
        updateResult -> checkUpdateResult(updateResult, checkResult -> {
          if (checkResult.failed()) {
            handleInsertError(checkResult.cause(), storeObject, resultHandler);
          } else {
            finishInsert(storeObject, resultHandler);
          }
        }));
  }

  private void handleInsertError(final Throwable t, final SqlStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
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
  private void checkUpdateResult(final AsyncResult<UpdateResult> updateResult,
      final Handler<AsyncResult<Void>> resultHandler) {
    if (updateResult.failed()) {
      Exception we = updateResult.cause() instanceof DuplicateKeyException
          ? (DuplicateKeyException) updateResult.cause()
          : new WriteException(updateResult.cause());
      resultHandler.handle(Future.failedFuture(we));
    } else {
      resultHandler.handle(Future.succeededFuture());
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void finishInsert(final SqlStoreObject storeObject, final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    try {
      Object id = storeObject.get(getMapper().getIdInfo().getField());
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
  protected void setIdValue(final Object id, final IStoreObject<T, ?> storeObject,
      final Handler<AsyncResult<Void>> resultHandler) {
    IProperty idField = getMapper().getIdInfo().getField();
    idField.getPropertyMapper().fromStoreObject(storeObject.getEntity(), storeObject, idField, resultHandler);
  }

}
