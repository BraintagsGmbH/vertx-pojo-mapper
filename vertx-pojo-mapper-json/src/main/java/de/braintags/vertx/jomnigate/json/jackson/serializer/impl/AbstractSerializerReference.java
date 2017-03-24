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

import de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractSerializerReference<T> implements ISerializationReference<T> {
  private String reference;
  private Future<T> future;

  /**
   * 
   */
  public AbstractSerializerReference(String reference, Future<T> future) {
    this.reference = reference;
    this.future = future;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#getFuture()
   */
  @Override
  public Future<T> getFuture() {
    return future;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#getReference()
   */
  @Override
  public String getReference() {
    return reference;
  }

  @Override
  public String toString() {
    return getReference();
  }

}
