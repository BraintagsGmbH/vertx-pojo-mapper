/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.datastoretest;

import de.braintags.io.vertx.pojomapper.IDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public interface IDatastoreContainer {
  public static final String PROPERTY = "IDatastoreContainer";

  public IDataStore getDataStore();

  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler);

  public void shutdown(Handler<AsyncResult<Void>> handler);

  /**
   * Drop all tables from the database which are not system tables
   * 
   * @param handler
   */
  public void dropTables(Handler<AsyncResult<Void>> handler);
}
