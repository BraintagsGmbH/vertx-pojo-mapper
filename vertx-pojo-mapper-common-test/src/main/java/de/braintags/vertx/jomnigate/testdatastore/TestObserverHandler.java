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

import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.jomnigate.testdatastore.observer.AfterMappingObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.BeforeDeleteObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.BeforeLoadObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.BeforeMappingObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.BeforeSaveObserver;
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

  @Test
  public void test_AfterMapping_SingleRecord(TestContext context) {
    AfterMappingObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<AfterMappingObserver> os = new ObserverDefinition<>(AfterMappingObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_MAPPING);
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    context.assertTrue(AfterMappingObserver.executed, "Observer wasn't executed");
  }

  @Test
  public void test_BeforeMapping_SingleRecord(TestContext context) {
    BeforeMappingObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeMappingObserver> os = new ObserverDefinition<>(BeforeMappingObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_MAPPING);
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    context.assertTrue(BeforeMappingObserver.executed, "Observer wasn't executed");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_AfterDelete_SingleRecord(TestContext context) {
    SimpleMapperObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<SimpleMapperObserver> os = new ObserverDefinition<>(SimpleMapperObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_DELETE);
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertFalse(SimpleMapperObserver.executed, "Observer should not execute here");
    context.assertEquals(-1, sm.intValue, "Expected NO Observer here");
    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertNotNull(tmp, "instance not found");

    IDelete<SimpleMapper> del = getDataStore(context).createDelete(SimpleMapper.class);
    del.add(tmp);
    delete(context, del, null, 1);

    context.assertTrue(SimpleMapperObserver.executed, "Observer wasn't executed");
    context.assertEquals(1, tmp.intValue, "Observer did not set number correct");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @SuppressWarnings({ "unused", "unchecked" })
  @Test
  public void test_BeforeDelete_SingleRecord(TestContext context) {
    BeforeLoadObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeDeleteObserver> os = new ObserverDefinition<>(BeforeDeleteObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_DELETE);
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertFalse(BeforeDeleteObserver.executed, "Observer should not execute here");
    context.assertEquals(-1, sm.intValue, "Expected NO Observer here");

    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertNotNull(tmp, "instance not found");

    IDelete<SimpleMapper> del = getDataStore(context).createDelete(SimpleMapper.class);
    del.add(tmp);
    delete(context, del, null, 1);
    context.assertTrue(BeforeDeleteObserver.executed, "Observer wasn't executed");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_AfterLoad_SingleRecord(TestContext context) {
    SimpleMapperObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<SimpleMapperObserver> os = new ObserverDefinition<>(SimpleMapperObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_LOAD);
    settings.getObserverSettings().add(os);
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertFalse(SimpleMapperObserver.executed, "Observer should not execute here");
    context.assertEquals(-1, sm.intValue, "Expected NO Observer here");

    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertTrue(SimpleMapperObserver.executed, "Observer wasn't executed");
    context.assertNotNull(tmp, "instance not found");
    context.assertEquals(1, tmp.intValue, "Observer did not set number correct");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @SuppressWarnings({ "unused", "unchecked" })
  @Test
  public void test_BeforeLoad_SingleRecord(TestContext context) {
    BeforeLoadObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeLoadObserver> os = new ObserverDefinition<>(BeforeLoadObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_LOAD);
    settings.getObserverSettings().add(os);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<SimpleMapper> sr = findAll(context, query);
    context.assertTrue(BeforeLoadObserver.executed, "Observer wasn't executed");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_AfterInsert_SingleRecord(TestContext context) {
    SimpleMapperObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<SimpleMapperObserver> os = new ObserverDefinition<>(SimpleMapperObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    settings.getObserverSettings().add(os);
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertTrue(SimpleMapperObserver.executed, "Observer wasn't executed");
    context.assertEquals(1, sm.intValue, "Observer did not set number correct");

    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertNotNull(tmp, "instance not found");
    context.assertEquals(-1, tmp.intValue, "handler afterSave should not save value");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_BeforeInsert_SingleRecord(TestContext context) {
    BeforeSaveObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeSaveObserver> os = new ObserverDefinition<>(BeforeSaveObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    settings.getObserverSettings().add(os);
    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertTrue(BeforeSaveObserver.executed, "Observer wasn't executed");
    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertNotNull(tmp, "instance not found");
    context.assertEquals(1, tmp.intValue, "Observer did not set number correct");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_BeforeUpdate_SingleRecord(TestContext context) {
    BeforeSaveObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeSaveObserver> os = new ObserverDefinition<>(BeforeSaveObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_UPDATE);
    settings.getObserverSettings().add(os);

    SimpleMapper sm = new SimpleMapper("testname", "nix");
    sm.intValue = -1;
    saveRecord(context, sm);
    context.assertFalse(BeforeSaveObserver.executed, "Observer should not be executed for INSERT");
    SimpleMapper tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertEquals(-1, tmp.intValue, "Observer must not set the value by INSERT");

    tmp.name = "updated name";
    saveRecord(context, sm);
    context.assertTrue(BeforeSaveObserver.executed, "Observer wasn't executed by UPDATE");

    tmp = findRecordByID(context, SimpleMapper.class, sm.id);
    context.assertNotNull(tmp, "instance not found");
    context.assertEquals(1, tmp.intValue, "Observer did not set number correct");
  }

  /**
   * The value of the field intValue must be set to 1 through the observer
   * 
   * @param context
   */
  @Test
  public void test_BeforeInsert_Selection(TestContext context) {
    clearTable(context, SimpleMapper.class);
    BeforeSaveObserver.executed = false;
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<BeforeSaveObserver> os = new ObserverDefinition<>(BeforeSaveObserver.class);
    os.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    settings.getObserverSettings().add(os);
    List<SimpleMapper> selection = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      SimpleMapper sm = new SimpleMapper("testname", "nix");
      sm.intValue = -1;
      selection.add(sm);
    }
    ResultContainer rc = saveRecords(context, selection);
    context.assertTrue(BeforeSaveObserver.executed, "Observer wasn't executed");

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<SimpleMapper> sr = findAll(context, query);
    context.assertEquals(selection.size(), sr.size(), "number of found records do not have the correct size");

    for (SimpleMapper sm : sr) {
      context.assertTrue(sm.intValue > 0, "Observer did not set number correct");
    }
  }

}
