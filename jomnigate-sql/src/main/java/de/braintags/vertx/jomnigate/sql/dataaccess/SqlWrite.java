/*
 * #%L
 * jomnigate-sql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.sql.dataaccess;

import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.json.dataaccess.JsonStoreObject;
import de.braintags.vertx.jomnigate.json.dataaccess.JsonWrite;
import de.braintags.vertx.jomnigate.sql.SqlDataStore;
import de.braintags.vertx.jomnigate.sql.SqlUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlWrite<T> extends JsonWrite<T> {

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlWrite(Class<T> mapperClass, SqlDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  protected void doInsert(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<Object>> resultHandler) {
    SqlStoreObject<T> sto = (SqlStoreObject<T>) storeObject;
    SqlSequence seq = sto.generateSqlInsertStatement();
    execute(seq, entity, storeObject, resultHandler);
  }

  @Override
  protected void doUpdate(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<Object>> resultHandler) {
    SqlStoreObject<T> sto = (SqlStoreObject<T>) storeObject;
    SqlSequence seq = sto.generateSqlUpdateStatement();
    execute(seq, entity, storeObject, resultHandler);
  }

  protected void execute(SqlSequence seq, T entity, JsonStoreObject<T> storeObject,
      Handler<AsyncResult<Object>> resultHandler) {
    SqlStoreObject<T> sto = (SqlStoreObject<T>) storeObject;
    if (seq.getParameters().isEmpty()) {
      SqlUtil.update((SqlDataStore) getDataStore(), seq.getSqlStatement(), result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(null));
        }
      });
    } else {
      SqlUtil.updateWithParams((SqlDataStore) getDataStore(), seq.getSqlStatement(), seq.getParameters(), result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(null));
        }
      });
    }
  }

  @Override
  protected boolean isDuplicateKeyException(Throwable e) {
    return e instanceof DuplicateKeyException;
  }

}
