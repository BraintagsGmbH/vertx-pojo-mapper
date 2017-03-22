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
import de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference;
import de.braintags.vertx.util.ExceptionUtil;
import io.vertx.core.Future;

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
  private boolean asArrayMembers;

  /**
   * @param future
   * @param reference
   * @param asArrayMembers
   *          if true, then the write entries are expected to be written as array; otherwise only the first member is
   *          written
   */
  public SerializationReference_Entity(Future<Object> future, String reference) {
    this.future = future;
    this.reference = reference;
    this.asArrayMembers = asArrayMembers;
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

  private String getResolvedReference(IDataStore<?, ?> datastore) {
    try {
      return ((JsonDatastore) datastore).getJacksonMapper().writeValueAsString(future.result());
    } catch (Exception e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#resolveReference(de.braintags.vertx.
   * jomnigate.IDataStore, java.lang.String)
   */
  @Override
  public String resolveReference(IDataStore<?, ?> datastore, String source) {
    String result = getResolvedReference(datastore);
    String re = source.replace("\"" + getReference() + "\"", result);
    return re;
  }
}