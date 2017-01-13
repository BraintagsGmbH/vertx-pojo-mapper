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
package de.braintags.io.vertx.pojomapper.init;

import de.braintags.io.vertx.pojomapper.IDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Implementations of IDataStoreInit are used to initialize an IDataStore by defined {@link DataStoreSettings}
 * 
 * @author Michael Remme
 * 
 */
public interface IDataStoreInit {

  /**
   * Initialize a new instance if {@link IDataStore} with the definintions inside the {@link DataStoreSettings}
   * 
   * @param vertx
   *          the instance of vertx to be used
   * @param settings
   *          the settings to be used for init
   * @param handler
   *          the handler to be informed about the created {@link IDataStore}
   * @return an initialized instance of {@link IDataStore}
   * 
   */
  public void initDataStore(Vertx vertx, DataStoreSettings settings, Handler<AsyncResult<IDataStore>> handler);
}
