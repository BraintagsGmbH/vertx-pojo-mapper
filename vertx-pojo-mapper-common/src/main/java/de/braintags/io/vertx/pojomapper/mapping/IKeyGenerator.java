/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.KeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.Key;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * The IKeyGenerator is used to generate primary keys for new instances of an {@link IMapper}. The IKeyGenerator can be
 * set per IMapper by using the annotation
 * 
 * @author Michael Remme
 * 
 */
public interface IKeyGenerator {
  /**
   * The name of the property which is used to set the default {@link IKeyGenerator} used by a datastore.
   */
  public static final String DEFAULT_KEY_GENERATOR = "defaultKeyGenerator";

  /**
   * Get the name of the IKeyGenerator. This is the name, which can be set as value for the annotation
   * {@link KeyGenerator} and by which the instance can be requested by the {@link IDataStore#getKeyGenerator(String)}
   * 
   * @return the name of the current instance
   */
  public String getName();

  /**
   * Generates a key and returns it
   * 
   * @param mapper
   *          the mapper to generate a key for
   * @param handler
   *          the handler to deliver the key to
   */
  void generateKey(IMapper mapper, Handler<AsyncResult<Key>> handler);

}
