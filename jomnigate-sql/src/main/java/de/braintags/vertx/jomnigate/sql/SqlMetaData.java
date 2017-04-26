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
package de.braintags.vertx.jomnigate.sql;

import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

/**
 * implementation of {@link IDataStoreMetaData} for sql based datastores
 * 
 * @author Michael Remme
 * 
 */
public class SqlMetaData implements IDataStoreMetaData {
  private static final String SELECT_VERSION = "SELECT VERSION()";
  private SqlDataStore datastore;
  private String version;

  /**
   * @param datastore
   */
  public SqlMetaData(SqlDataStore datastore) {
    this.datastore = datastore;
  }

  @Override
  public void getVersion(Handler<AsyncResult<String>> handler) {
    if (version != null) {
      handler.handle(Future.succeededFuture(version));
    } else {
      Future<ResultSet> f = SqlUtil.query(datastore, SELECT_VERSION);
      f.setHandler(result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          version = result.result().getResults().get(0).getString(0);
          handler.handle(Future.succeededFuture(version));
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStoreMetaData#getIndexInfo(java.lang.String,
   * de.braintags.vertx.jomnigate.mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  public void getIndexInfo(String indexName, IMapper mapper, Handler<AsyncResult<Object>> handler) {
    Future<JsonObject> f = SqlUtil.getIndexInfo(datastore, mapper.getTableInfo().getName(), indexName);
    f.setHandler(res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result()));
      }
    });
  }

}
