/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mongo;

import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoMetaData implements IDataStoreMetaData {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoMetaData.class);

  private final MongoDataStore ds;
  private JsonObject buildInfo;

  /**
   * Constructor
   * 
   * @param client
   *          the {@link MongoClient} to be used
   */
  public MongoMetaData(final MongoDataStore ds) {
    this.ds = ds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStoreMetaData#getVersion(io.vertx.core.Handler)
   */
  @Override
  public void getVersion(final Handler<AsyncResult<String>> handler) {
    if (buildInfo != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Buildinfo exists already: " + buildInfo);
      }
      handler.handle(Future.succeededFuture(buildInfo.getString("version")));
      return;
    }
    JsonObject command = new JsonObject().put("buildInfo", 1);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("fetching buildinfo with: " + command);
    }
    ((MongoClient) ds.getClient()).runCommand("buildInfo", command, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        buildInfo = result.result();
        handler.handle(Future.succeededFuture(buildInfo.getString("version")));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStoreMetaData#getIndexInfo(java.lang.String,
   * de.braintags.vertx.jomnigate.mapping.IMapper)
   */
  @Override
  public void getIndexInfo(final String indexName, final IMapper mapper, final Handler<AsyncResult<Object>> handler) {
    MongoUtil.getIndexes(ds, mapper.getTableInfo().getName(), result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object returnIndex = null;
        JsonArray array = result.result().getJsonObject("cursor").getJsonArray("firstBatch");
        for (Object jo : array) {
          if (jo instanceof JsonObject && ((JsonObject) jo).getString("name").equals(indexName)) {
            returnIndex = jo;
            break;
          }
        }
        handler.handle(Future.succeededFuture(returnIndex));
      }
    });
  }

}
