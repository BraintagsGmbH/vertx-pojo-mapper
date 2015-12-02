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
package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.ExceptionUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestHelper {
  private static final io.vertx.core.logging.Logger logger = io.vertx.core.logging.LoggerFactory
      .getLogger(TestHelper.class);

  public static Vertx vertx;
  private static IDatastoreContainer datastoreContainer;

  /**
   * 
   */
  private TestHelper() {
  }

  public static IDatastoreContainer getDatastoreContainer() {
    if (datastoreContainer == null) {
      try {
        startup();
      } catch (Exception e) {
        throw ExceptionUtil.createRuntimeException(e);
      }
    }
    return datastoreContainer;
  }

  /**
   * Init the datastore
   */
  public static final void startup() throws Exception {
    logger.info("setup");
    CountDownLatch latch = new CountDownLatch(1);
    vertx = Vertx.vertx(getOptions());
    String property = System.getProperty(IDatastoreContainer.PROPERTY);
    if (property == null) {
      throw new ParameterRequiredException("Need the parameter " + IDatastoreContainer.PROPERTY
          + ". Start the test with -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer for instance");
    }
    datastoreContainer = (IDatastoreContainer) Class.forName(property).newInstance();
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    logger.info("wait for startup of datastore");
    datastoreContainer.startup(vertx, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      logger.info("datastore started");
      Objects.requireNonNull(getDataStore(), "The datastore must not be null");
      latch.countDown();
    });

    latch.await();
    if (err.isError())
      throw err.getRuntimeException();
  }

  /**
   * shutdown datastore
   */
  public static final void shutdown() throws Exception {
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    CountDownLatch latch = new CountDownLatch(1);
    datastoreContainer.shutdown(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        err.setThrowable(result.cause());
      }
      vertx.close(vr -> {
        if (vr.failed()) {
          logger.error("", result.cause());
          err.setThrowable(result.cause());
        }
        latch.countDown();
      });
    });
    latch.await();
    if (err.isError())
      throw err.getRuntimeException();

  }

  /**
   * Creates the VertxOptions by checking System variables BlockedThreadCheckInterval and WarningExceptionTime
   */
  public static VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    String blockedThreadCheckInterval = System.getProperty("BlockedThreadCheckInterval");
    if (blockedThreadCheckInterval != null) {
      logger.info("setting setBlockedThreadCheckInterval to " + blockedThreadCheckInterval);
      options.setBlockedThreadCheckInterval(Long.parseLong(blockedThreadCheckInterval));
    }
    String warningExceptionTime = System.getProperty("WarningExceptionTime");
    if (warningExceptionTime != null) {
      logger.info("setting setWarningExceptionTime to " + warningExceptionTime);
      options.setWarningExceptionTime(Long.parseLong(warningExceptionTime));
    }
    return options;
  }

  public static IDataStore getDataStore() {
    return datastoreContainer.getDataStore();
  }
}
