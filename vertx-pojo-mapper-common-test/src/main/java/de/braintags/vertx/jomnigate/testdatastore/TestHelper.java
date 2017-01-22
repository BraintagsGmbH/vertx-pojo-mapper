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

import java.util.concurrent.CountDownLatch;

import de.braintags.vertx.BtVertxTestBase;
import de.braintags.vertx.keygenerator.KeyGeneratorSettings;
import de.braintags.vertx.keygenerator.KeyGeneratorVerticle;
import de.braintags.vertx.keygenerator.impl.MongoKeyGenerator;
import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.vertx.util.ErrorObject;
import de.braintags.vertx.util.ExceptionUtil;
import de.braintags.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

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
  private static KeyGeneratorVerticle keyGenVerticle;

  /**
   * 
   */
  private TestHelper() {
  }

  public static IDatastoreContainer getDatastoreContainer(TestContext context) {
    if (datastoreContainer == null) {
      try {
        startup(context);
      } catch (Exception e) {
        throw ExceptionUtil.createRuntimeException(e);
      }
    }
    return datastoreContainer;
  }

  /**
   * Init the datastore
   */
  public static final void startup(TestContext context) throws Exception {
    logger.info("setup");
    CountDownLatch latch = new CountDownLatch(1);
    vertx = Vertx.vertx(getOptions());
    String property = System.getProperty(IDatastoreContainer.PROPERTY);
    if (property == null) {
      throw new ParameterRequiredException("Need the parameter " + IDatastoreContainer.PROPERTY
          + ". Start the test with -DIDatastoreContainer=de.braintags.vertx.jomnigate.mysql.MySqlDataStoreContainer for instance");
    }
    datastoreContainer = (IDatastoreContainer) Class.forName(property).newInstance();
    ErrorObject<Void> err = new ErrorObject<>(null);
    logger.info("wait for startup of datastore");
    datastoreContainer.startup(vertx, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      } else if (getDataStore() == null) {
        err.setThrowable(new NullPointerException("The datastore must not be null"));
      } else {
        logger.info("datastore started");
      }
      latch.countDown();
    });
    latch.await();
    if (err.isError()) {
      throw err.getRuntimeException();
    } else {
      if (datastoreContainer.getDataStore().getDefaultKeyGenerator() instanceof DefaultKeyGenerator) {
        startKeyGeneratorVerticle(context);
      }
    }

  }

  /**
   * shutdown datastore
   */
  public static final void shutdown() throws Exception {
    ErrorObject<Void> err = new ErrorObject<>(null);
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

  public static void startKeyGeneratorVerticle(TestContext context) {
    if (keyGenVerticle == null) {
      logger.info("init Keygenerator");
      Async async = context.async();
      keyGenVerticle = createKeyGenerator(context);
      vertx.deployVerticle(keyGenVerticle, result -> {
        if (result.failed()) {
          context.fail(result.cause());
          async.complete();
        } else {
          async.complete();
        }
      });
      async.awaitSuccess();
    }

  }

  private static KeyGeneratorVerticle createKeyGenerator(TestContext context) {
    KeyGeneratorSettings settings = new KeyGeneratorSettings();
    settings.setKeyGeneratorClass(MongoKeyGenerator.class);
    settings.getGeneratorProperties().put(MongoKeyGenerator.COLLECTTION_PROP, "keyGenSequence");
    settings.getGeneratorProperties().put(MongoKeyGenerator.START_MONGO_LOCAL_PROP,
        System.getProperty(MongoKeyGenerator.START_MONGO_LOCAL_PROP));
    return new KeyGeneratorVerticle(settings);
  }

  /**
   * Creates the VertxOptions by checking System variables BlockedThreadCheckInterval and WarningExceptionTime
   * 
   * @deprecated Use {@link BtVertxTestBase#getOptions()}
   */
  @Deprecated
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
