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

import org.junit.Test;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver_NoDefaultConstructor;
import io.vertx.ext.unit.TestContext;

/**
 * Tests to improve correct mapping information for defined observers ( or by settings or annotation )
 * 
 * @author Michael Remme
 * 
 */
public class TestObserverMapping extends DatastoreBaseTest {

  /**
   * Defines an observer, which should be executed for any event and mapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_All(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    settings.getObserverSettings().clear();
    settings.getObserverSettings().add(new ObserverSettings<>(TestObserver.class));
    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver(context, mapper, ObserverEventType.AFTER_DELETE, 1);
    checkObserver(context, mapper, ObserverEventType.AFTER_LOAD, 1);
    checkObserver(context, mapper, ObserverEventType.AFTER_SAVE, 1);
    checkObserver(context, mapper, ObserverEventType.BEFORE_DELETE, 1);
    checkObserver(context, mapper, ObserverEventType.BEFORE_LOAD, 1);
    checkObserver(context, mapper, ObserverEventType.BEFORE_SAVE, 1);
  }

  /**
   * Define an observer class, which will cause an instantiation exception ( no default constructor )
   * 
   * @param context
   */
  @Test
  public void testClassNotFound_ObserverClass(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    settings.getObserverSettings().clear();
    settings.getObserverSettings().add(new ObserverSettings<>(TestObserver_NoDefaultConstructor.class));
    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);

  }

  private void checkObserver(TestContext context, IMapper<?> mapper, ObserverEventType eventType, int expectedResult) {
    context.assertEquals(expectedResult, mapper.getObserver(ObserverEventType.BEFORE_SAVE),
        "Expected number of observers wrong for event " + eventType);
  }

}
