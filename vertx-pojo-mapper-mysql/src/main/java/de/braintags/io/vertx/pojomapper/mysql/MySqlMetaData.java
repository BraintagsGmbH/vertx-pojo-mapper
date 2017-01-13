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

package de.braintags.io.vertx.pojomapper.mysql;

import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Meta information about the connected database
 * 
 * @author Michael Remme
 * 
 */

public class MySqlMetaData implements IDataStoreMetaData {
  private static final String SELECT_VERSION = "SELECT VERSION()";
  private MySqlDataStore datastore;
  private String version;

  /**
   * Constructor for the Metadata
   * 
   * @param sqlClient
   *          the client to be used
   */
  public MySqlMetaData(MySqlDataStore datastore) {
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStoreMetaData#getVersion(io.vertx.core.Handler)
   */
  @Override
  public void getVersion(Handler<AsyncResult<String>> handler) {
    if (version != null) {
      handler.handle(Future.succeededFuture(version));
      return;
    }

    SqlUtil.query(datastore.getSqlClient(), SELECT_VERSION, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        version = result.result().getResults().get(0).getString(0);
        handler.handle(Future.succeededFuture(version));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStoreMetaData#getIndexInfo(java.lang.String,
   * de.braintags.io.vertx.pojomapper.mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  public void getIndexInfo(String indexName, IMapper mapper, Handler<AsyncResult<Object>> handler) {
    SqlUtil.getIndexInfo(datastore, mapper.getTableInfo().getName(), indexName, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(result.result()));
      }
    });
  }

}
