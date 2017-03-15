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
package de.braintags.vertx.jomnigate.json.jackson.deserializer;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;

import de.braintags.vertx.jomnigate.IDataStore;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreDeserializer<T> extends StdDeserializer<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractDataStoreDeserializer.class);

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;
  private IDataStore datastore;
  private Annotated annotated;

  /**
   * @param vc
   */
  public AbstractDataStoreDeserializer(IDataStore datastore, Annotated annotated) {
    super((Class<?>) null);
    this.datastore = datastore;
    this.annotated = annotated;
  }

  /**
   * @return the datastore
   */
  public IDataStore getDatastore() {
    return datastore;
  }

  /**
   * @return the annotated
   */
  public Annotated getAnnotated() {
    return annotated;
  }

}
