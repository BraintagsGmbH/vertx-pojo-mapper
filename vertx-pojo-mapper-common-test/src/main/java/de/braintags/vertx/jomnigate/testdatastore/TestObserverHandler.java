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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.jomnigate.testdatastore.observer.SimpleMapperObserver;
import io.vertx.ext.unit.TestContext;

/**
 * Tests to improve correct mapping information for defined observers ( or by settings or annotation )
 * 
 * @author Michael Remme
 * 
 */
public class TestObserverHandler extends AbstractObserverTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestObserverHandler.class);

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_BeforeSave_SingleRecord(TestContext context) {
    SimpleMapperObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverSettings<SimpleMapperObserver> os = new ObserverSettings<>(SimpleMapperObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_SAVE);
    settings.getObserverSettings().add(os);
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertTrue(SimpleMapperObserver.executed, "Observer wasn't executed");
    context.assertNotNull(tmp, "instance not found");
    context.assertEquals(1, sm.intValue, "Observer did not set number correct");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_BeforeSave_Selection(TestContext context) {
    clearTable(context, SimpleMapper.class);
    SimpleMapperObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverSettings<SimpleMapperObserver> os = new ObserverSettings<>(SimpleMapperObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_SAVE);
    settings.getObserverSettings().add(os);
    List<SimpleMapper> selection = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      SimpleMapper sm = new SimpleMapper("testname", "nix");
      sm.intValue = -1;
      selection.add(sm);
    }
    ResultContainer rc = saveRecords(context, selection);
    context.assertTrue(SimpleMapperObserver.executed, "Observer wasn't executed");

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<SimpleMapper> sr = findAll(context, query);
    context.assertEquals(selection.size(), sr.size(), "number of found records do not have the correct size");

    for (SimpleMapper sm : sr) {
      context.assertTrue(sm.intValue > 0, "Observer did not set number correct");
    }
  }

}
