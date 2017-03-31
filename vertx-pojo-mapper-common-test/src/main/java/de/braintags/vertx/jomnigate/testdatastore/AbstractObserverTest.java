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

import java.util.List;

import org.junit.After;
import org.junit.Before;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.util.ExceptionUtil;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractObserverTest extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractObserverTest.class);

  /*
   * #####################################################################
   * 
   * #####################################################################
   */

  @After
  @Before
  public void reset(TestContext context) {
    getDataStore(context).getMapperFactory().reset();
    DataStoreSettings settings = getDataStore(context).getSettings();
    serializeDeserializeSettings(context, settings);
    settings.getObserverSettings().clear();
  }

  /**
   * Checking that serialization by jackson is running
   */
  @SuppressWarnings("unused")
  private void serializeDeserializeSettings(TestContext context, DataStoreSettings settings) {
    try {
      String src = Json.encodePrettily(settings);
      DataStoreSettings settings2 = Json.decodeValue(src, DataStoreSettings.class);
    } catch (Exception e) {
      LOGGER.error("", e);
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  protected void checkObserver_AllEvents(TestContext context, IMapper<?> mapper, int expectedResult) {
    checkObserver(context, mapper, expectedResult, ObserverEventType.values());
  }

  protected void checkObserver(TestContext context, IMapper<?> mapper, int expectedResult,
      ObserverEventType... eventTypes) {
    for (ObserverEventType t : eventTypes) {
      LOGGER.debug("checking event type: " + t.name());
      List<IObserver> rl = mapper.getObserverHandler().getObserver(t);
      context.assertEquals(expectedResult, rl.size(), "Expected number of observers wrong for event " + t);
    }
  }

}
