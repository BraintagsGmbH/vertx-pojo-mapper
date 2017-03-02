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
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
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

  public DataStoreSettings createSettings();

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
