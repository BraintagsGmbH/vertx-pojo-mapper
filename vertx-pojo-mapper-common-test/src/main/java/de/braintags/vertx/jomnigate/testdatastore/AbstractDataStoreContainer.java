/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreContainer implements IDatastoreContainer {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractDataStoreContainer.class);

  public static String DEFAULT_KEY_GENERATOR = DefaultKeyGenerator.NAME;
  private IDataStore datastore;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.IDatastoreContainer#getDataStore()
   */
  @Override
  public final IDataStore getDataStore() {
    return datastore;
  }

  protected void setDatastore(IDataStore datastore) {
    this.datastore = datastore;
  }

}
