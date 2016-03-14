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

package de.braintags.io.vertx.pojomapper.testdatastore;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
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
  public void dropTable(String tablename, Handler<AsyncResult<Void>> handler);

  /**
   * Delete all records from the given table WITHOUT mapping!
   * 
   * @param handler
   */
  public void clearTable(String tablename, Handler<AsyncResult<Void>> handler);

  /**
   * Get the expected ITypeHandler for the given test class. With this method the expected {@link ITypeHandler} can be
   * overwritten per datastore driver
   * 
   * @param testClass
   *          the class as instance of {@link AbstractTypeHandlerTest} to be tested
   * @param defaultName
   *          the default typehandler
   * @return the expected typehandler class name
   */
  public String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass, String defaultName);
}
