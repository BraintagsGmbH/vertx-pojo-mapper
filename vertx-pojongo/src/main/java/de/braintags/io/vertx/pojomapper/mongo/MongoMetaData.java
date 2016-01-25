/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mongo;

import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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

  private MongoClient client;
  private JsonObject buildInfo;

  /**
   * Constructor
   * 
   * @param client
   *          the {@link MongoClient} to be used
   */
  public MongoMetaData(MongoClient client) {
    this.client = client;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.IDataStoreMetaData#getVersion(io.vertx.core.Handler)
   */
  @Override
  public void getVersion(Handler<AsyncResult<String>> handler) {
    if (buildInfo != null) {
      LOGGER.debug("Buildinfo exists already: " + buildInfo);
      handler.handle(Future.succeededFuture(buildInfo.getString("version")));
      return;
    }
    JsonObject command = new JsonObject().put("buildInfo", 1);
    LOGGER.debug("fetching buildinfo with: " + command);
    client.runCommand("buildInfo", command, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        buildInfo = result.result();
        handler.handle(Future.succeededFuture(buildInfo.getString("version")));
      }
    });
  }

}
