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
package de.braintags.io.vertx.pojomapper.mysql.init;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MySqlDataStoreinit implements IDataStoreInit {

  /**
   * 
   */
  public MySqlDataStoreinit() {
  }

  /* (non-Javadoc)
   * @see de.braintags.io.vertx.pojomapper.init.IDataStoreInit#initDataStore(io.vertx.core.Vertx, de.braintags.io.vertx.pojomapper.init.DataStoreSettings, io.vertx.core.Handler)
   */
  @Override
  public void initDataStore(Vertx vertx, DataStoreSettings settings, Handler<AsyncResult<IDataStore>> handler) {
  }

}
