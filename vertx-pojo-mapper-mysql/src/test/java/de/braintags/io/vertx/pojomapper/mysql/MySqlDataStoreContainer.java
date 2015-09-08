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

package de.braintags.io.vertx.pojomapper.mysql;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.test.IDatastoreContainer;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStoreContainer implements IDatastoreContainer {
  private MySqlDataStore datastore;

  /**
   * 
   */
  public MySqlDataStoreContainer() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.test.IDatastoreContainer#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

}
