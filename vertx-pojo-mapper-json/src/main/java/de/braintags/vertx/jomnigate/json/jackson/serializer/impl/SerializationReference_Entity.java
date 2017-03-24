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

import com.fasterxml.jackson.core.JsonFactory;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateGenerator;
import de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference;
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
public class SerializationReference_Entity implements ISerializationReference {
  private Future<Object> future;
  private String reference;
  private JOmnigateGenerator generator;

  /**
   * @param future
   * @param reference
   * @param generator
   *          the JOmnigateGenerator is needed for further serialization of referenced entity
   */
  public SerializationReference_Entity(Future<Object> future, String reference, JOmnigateGenerator generator) {
    this.future = future;
    this.reference = reference;
    this.generator = generator;
  }

  /**
   * Get the future, which was the result of storage of a referenced field content
   * 
   * @return the future
   */
  @Override
  public Future<Object> getFuture() {
    return future;
  }

  /**
   * Get the reference, which was placed inside the generated json and which will be replaced by the real id from out
   * of the Future
   * 
   * @return the reference
   */
  @Override
  public String getReference() {
    return reference;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#resolveReference(de.braintags.vertx.
   * jomnigate.IDataStore, java.lang.String)
   */
  @Override
  public Future<String> resolveReference(IDataStore<?, ?> datastore, String source) {
    Future<String> f = Future.future();
    getResolvedReference(datastore, res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        f.complete(res.result());
      }
    });
    return f;
  }

  private void getResolvedReference(IDataStore<?, ?> datastore, Handler<AsyncResult<String>> handler) {
    try {
      JsonFactory f = ((JsonDatastore) datastore).getJacksonMapper().getFactory();
      JOmnigateGenerator gen = JOmnigateFactory.createGenerator((JsonDatastore) datastore);
      // gen.setParentJomnigateGenerator(generator);
      ((JsonDatastore) datastore).getJacksonMapper().writer().writeValue(gen, future.result());
      String result = gen.getWriter().toString();
      gen.resolveReferences(datastore, result, handler);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public String toString() {
    return getReference();
  }
}