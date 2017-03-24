/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.jackson.serializer.impl;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.serializer.JOmnigateGenerator;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * SerializationReference stores information about references, which are written during serialization. It is used to
 * create the bridge between synchronous execution of jackson and async processing of the datastore on saving
 * instances.
 * 
 * @author Michael Remme
 *
 */
public class SerializationReference_Entity extends AbstractSerializerReference<Object> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SerializationReference_Entity.class);

  private JOmnigateGenerator generator;

  /**
   * @param future
   * @param reference
   * @param generator
   *          the JOmnigateGenerator is needed for further serialization of referenced entity
   */
  public SerializationReference_Entity(Future<Object> future, String reference, JOmnigateGenerator generator) {
    super(reference, future);
    this.generator = generator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#resolveReference(de.braintags.vertx.
   * jomnigate.IDataStore, java.lang.String)
   */
  @Override
  public Future<Void> resolveReference(IDataStore<?, ?> datastore, ResultObject<String> ro) {
    Future<Void> f = Future.future();
    getResolvedReference(datastore, res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        String result = ro.getResult().replace("\"" + getReference() + "\"", res.result());
        ro.setResult(result);
        f.complete();
      }
    });
    return f;
  }

  protected void getResolvedReference(IDataStore<?, ?> datastore, Handler<AsyncResult<String>> handler) {
    try {
      JOmnigateGenerator gen = JOmnigateFactory.createGenerator((JsonDatastore) datastore);
      Object result = getFuture().result();
      ((JsonDatastore) datastore).getJacksonMapper().writer().writeValue(gen, result);
      gen.getResult(handler);
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

}