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
package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;

/**
 * An abstract implementation of IMapperFactory
 * 
 * @author Michael Remme
 */
public abstract class AbstractMapperFactory implements IMapperFactory {
  private IDataStore<?, ?> datastore;

  /**
   * 
   */
  public AbstractMapperFactory(IDataStore<?, ?> dataStore) {
    this.datastore = dataStore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getDataStore()
   */
  @Override
  public final IDataStore<?, ?> getDataStore() {
    return datastore;
  }

}
