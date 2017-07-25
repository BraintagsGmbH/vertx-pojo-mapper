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

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.observer.impl.JsonSerializationObserver;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

/**
 * Tests to improve correct mapping information for defined observers ( or by settings or annotation )
 * 
 * @author Michael Remme
 * 
 */
public class TestJsonSerializationObserver extends AbstractObserverTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestJsonSerializationObserver.class);

  @Test
  public void test_Serialization(TestContext context) {
    Vertx vertx = this.getDataStore(context).getVertx();
    LOGGER.info("EXISTS: " + vertx.fileSystem()
        .existsBlocking("/Users/mremme/workspace/vertx/vertx-pojo-mapper/vertx-pojo-mapper-common-test/tmp"));

  }

  @Test
  public void testAfterInsertAndUpdate(TestContext context) throws IOException {
    File logDir = getLogDir();
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<JsonSerializationObserver> os = new ObserverDefinition<>(JsonSerializationObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    os.getEventTypeList().add(ObserverEventType.AFTER_UPDATE);
    os.getObserverProperties().setProperty(JsonSerializationObserver.DIRECTORY_PROPERTY, logDir.getAbsolutePath());
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    sm.intValue = 5;
    sm.name = "newNAME";
    saveRecord(context, sm);
    checkResult(context, logDir, 2, 500);
  }

  @Test
  public void testAfterInsert(TestContext context) throws IOException {
    File logDir = getLogDir();
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<JsonSerializationObserver> os = new ObserverDefinition<>(JsonSerializationObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    os.getObserverProperties().setProperty(JsonSerializationObserver.DIRECTORY_PROPERTY, logDir.getAbsolutePath());
    settings.getObserverSettings().add(os);
    LOGGER.debug(Json.encodePrettily(os));
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    checkResult(context, logDir, 1, 500);
  }

  @Test
  public void testDirectoryNotSet(TestContext context) throws IOException {
    File logDir = getLogDir();
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<JsonSerializationObserver> os = new ObserverDefinition<>(JsonSerializationObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    settings.getObserverSettings().add(os);
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    try {
      saveRecord(context, sm);
      context.fail("we are expecting an exception, cause the log directory is not set");
    } catch (Throwable e) {
      // we expect this
    }
  }

  private void checkResult(TestContext context, File logDir, int fileCount, long waitTicks) {
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < waitTicks) {
      File[] files = logDir.listFiles((dir, fileName) -> fileName.endsWith("json"));
      if (files.length == fileCount) {
        break;
      }
    }
    context.assertEquals(fileCount, logDir.listFiles().length, "expected files to be created");

  }

  private File getLogDir() throws IOException {
    File logDir = new File("tmp/jsonSerDirTmp");
    File[] fl = logDir.listFiles();
    for (File f : fl) {
      f.delete();
    }
    if (logDir.listFiles().length > 0) {
      throw new IllegalArgumentException("the directory was not cleaned");
    }
    logDir.mkdir();
    LOGGER.debug("FILE CREATION IN " + logDir.getAbsolutePath());
    return logDir;
  }

}
