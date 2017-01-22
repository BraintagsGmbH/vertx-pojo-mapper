/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapping.impl.keygen;

import de.braintags.vertx.keygenerator.KeyGeneratorVerticle;
import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * The DefaultKeyGenerator is using the vertx eventbus to request a new key by using the {@link KeyGeneratorVerticle}
 * 
 * @author mremme
 * 
 */
public class DefaultKeyGenerator extends AbstractKeyGenerator {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DefaultKeyGenerator.class);
  public static final String NAME = "DefaultKeyGenerator";
  private Vertx vertx;

  /**
   * @param name
   * @param datastore
   */
  public DefaultKeyGenerator(IDataStore datastore) {
    super(NAME, datastore);
    vertx = datastore.getVertx();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IKeyGenerator#generateKey(de.braintags.vertx.jomnigate.mapping.
   * IMapper, io.vertx.core.Handler)
   */
  @Override
  public void generateKey(IMapper<?> mapper, Handler<AsyncResult<Key>> handler) {
    vertx.eventBus().send(KeyGeneratorVerticle.SERVICE_NAME, mapper.getMapperClass().getSimpleName(), result -> {
      if (result.failed()) {
        LOGGER.error(result.cause());
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(new Key(result.result().body())));
      }
    });
  }

}
