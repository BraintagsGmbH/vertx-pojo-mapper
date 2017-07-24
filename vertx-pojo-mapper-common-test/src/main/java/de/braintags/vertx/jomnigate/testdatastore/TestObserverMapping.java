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

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.init.ObserverMapperSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.ObserverAnnotatedMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.ObserverAnnotatedMapper_TwoEvents;
import de.braintags.vertx.jomnigate.testdatastore.mapper.Person;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.TriggerMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BooleanMapper;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver2;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver3;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver4;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver_NoDefaultConstructor;
import io.vertx.ext.unit.TestContext;

/**
 * Tests to improve correct mapping information for defined observers ( or by settings or annotation )
 * 
 * @author Michael Remme
 * 
 */
public class TestObserverMapping extends AbstractObserverTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestObserverMapping.class);

  /**
   * Defines an observer, which should be executed for any event instanceof BaseRecord. TriggerMapper should not be
   * handled by this mapper
   * 
   * @param context
   */
  @Test
  public void testObserverWithProperties(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver4> os = new ObserverDefinition<>(TestObserver4.class);
    os.setPriority(500);
    settings.getObserverSettings().add(os);

    ObserverDefinition<TestObserver2> os2 = new ObserverDefinition<>(TestObserver2.class);
    os2.setPriority(200);
    settings.getObserverSettings().add(os2);

    ObserverDefinition<TestObserver3> os3 = new ObserverDefinition<>(TestObserver3.class);
    os3.setPriority(501);
    settings.getObserverSettings().add(os3);

    IMapper<ObserverAnnotatedMapper> mapper = getDataStore(context).getMapperFactory()
        .getMapper(ObserverAnnotatedMapper.class);
    checkObserver_AllEvents(context, mapper, 4);

    List<IObserver> ol = mapper.getObserverHandler().getObserver(ObserverEventType.AFTER_DELETE);
    context.assertTrue(ol.get(0).getClass() == TestObserver.class, "wrong sort by priority");
    context.assertTrue(ol.get(1).getClass() == TestObserver3.class, "wrong sort by priority");
    context.assertTrue(ol.get(2).getClass() == TestObserver4.class, "wrong sort by priority");
    context.assertTrue(ol.get(3).getClass() == TestObserver2.class, "wrong sort by priority");

  }

  /**
   * Defines an observer, which should be executed for any event instanceof BaseRecord. TriggerMapper should not be
   * handled by this mapper
   * 
   * @param context
   */
  @Test
  public void testObserverAndGlobal_Priority(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver4> os = new ObserverDefinition<>(TestObserver4.class);
    os.setPriority(500);
    settings.getObserverSettings().add(os);

    ObserverDefinition<TestObserver2> os2 = new ObserverDefinition<>(TestObserver2.class);
    os2.setPriority(200);
    settings.getObserverSettings().add(os2);

    ObserverDefinition<TestObserver3> os3 = new ObserverDefinition<>(TestObserver3.class);
    os3.setPriority(501);
    settings.getObserverSettings().add(os3);

    IMapper<ObserverAnnotatedMapper> mapper = getDataStore(context).getMapperFactory()
        .getMapper(ObserverAnnotatedMapper.class);
    checkObserver_AllEvents(context, mapper, 4);

    List<IObserver> ol = mapper.getObserverHandler().getObserver(ObserverEventType.AFTER_DELETE);
    context.assertTrue(ol.get(0).getClass() == TestObserver.class, "wrong sort by priority");
    context.assertTrue(ol.get(1).getClass() == TestObserver3.class, "wrong sort by priority");
    context.assertTrue(ol.get(2).getClass() == TestObserver4.class, "wrong sort by priority");
    context.assertTrue(ol.get(3).getClass() == TestObserver2.class, "wrong sort by priority");

  }

  /**
   * Checks a mapper, which is annotated for usage of an observer
   * 
   * @param context
   */
  @Test
  public void testMapperObserverAndGlobal_TwoEvents(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver2> os = new ObserverDefinition<>(TestObserver2.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_DELETE);
    os.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    settings.getObserverSettings().add(os);

    IMapper<ObserverAnnotatedMapper_TwoEvents> mapper = getDataStore(context).getMapperFactory()
        .getMapper(ObserverAnnotatedMapper_TwoEvents.class);

    checkObserver(context, mapper, 2, ObserverEventType.AFTER_DELETE);
    checkObserver(context, mapper, 1, ObserverEventType.BEFORE_DELETE);
    checkObserver(context, mapper, 1, ObserverEventType.BEFORE_INSERT);
    checkObserver(context, mapper, 0, ObserverEventType.AFTER_LOAD, ObserverEventType.AFTER_INSERT,
        ObserverEventType.BEFORE_LOAD);
  }

  /**
   * Checks a mapper, which is annotated for usage of an observer only for two events
   * 
   * @param context
   */
  @Test
  public void testMapperObserver_TwoEvents(TestContext context) {
    IMapper<ObserverAnnotatedMapper_TwoEvents> mapper = getDataStore(context).getMapperFactory()
        .getMapper(ObserverAnnotatedMapper_TwoEvents.class);
    checkObserver(context, mapper, 1, ObserverEventType.AFTER_DELETE, ObserverEventType.BEFORE_DELETE);
    checkObserver(context, mapper, 0, ObserverEventType.AFTER_LOAD, ObserverEventType.AFTER_INSERT,
        ObserverEventType.BEFORE_LOAD, ObserverEventType.BEFORE_INSERT);
  }

  /**
   * Checks a mapper, which is annotated for usage of an observer
   * 
   * @param context
   */
  @Test
  public void testMapperObserver_AllEvents(TestContext context) {
    IMapper<ObserverAnnotatedMapper> mapper = getDataStore(context).getMapperFactory()
        .getMapper(ObserverAnnotatedMapper.class);
    checkObserver_AllEvents(context, mapper, 1);

  }

  /**
   * Defines an observer, which should be executed for any event on mappers, which are annotated with
   * {@link JsonTypeInfo}
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_Annotated(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();

    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);
    os.getMapperSettings().add(new ObserverMapperSettings(JsonTypeInfo.class));
    settings.getObserverSettings().add(os);

    IMapper<PolyMapper> mapper = getDataStore(context).getMapperFactory().getMapper(PolyMapper.class);
    checkObserver_AllEvents(context, mapper, 1);

    IMapper<SimpleMapper> mapper2 = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver_AllEvents(context, mapper2, 0);

  }

  /**
   * Defines an observer, which should be executed for any event instanceof BaseRecord. TriggerMapper should not be
   * handled by this mapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_Priority(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();

    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);
    os.setPriority(500);
    settings.getObserverSettings().add(os);

    ObserverDefinition<TestObserver2> os2 = new ObserverDefinition<>(TestObserver2.class);
    os2.setPriority(200);
    settings.getObserverSettings().add(os2);

    ObserverDefinition<TestObserver3> os3 = new ObserverDefinition<>(TestObserver3.class);
    os3.setPriority(501);
    settings.getObserverSettings().add(os3);

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver_AllEvents(context, mapper, 3);

    List<IObserver> ol = mapper.getObserverHandler().getObserver(ObserverEventType.AFTER_DELETE);
    context.assertTrue(ol.get(0).getClass() == TestObserver3.class, "wrong sort by priority");
    context.assertTrue(ol.get(1).getClass() == TestObserver.class, "wrong sort by priority");
    context.assertTrue(ol.get(2).getClass() == TestObserver2.class, "wrong sort by priority");

  }

  /**
   * Defines an observer, which should be executed for any event instanceof BaseRecord. TriggerMapper should not be
   * handled by this mapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_InstanceOf(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);

    ObserverMapperSettings oms = new ObserverMapperSettings(BaseRecord.class.getName());
    oms.setInstanceOf(true);
    os.getMapperSettings().add(oms);
    settings.getObserverSettings().add(os);

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver_AllEvents(context, mapper, 1);

    IMapper<BooleanMapper> mapper2 = getDataStore(context).getMapperFactory().getMapper(BooleanMapper.class);
    checkObserver_AllEvents(context, mapper2, 1);

    IMapper<Person> mapper3 = getDataStore(context).getMapperFactory().getMapper(Person.class);
    checkObserver_AllEvents(context, mapper3, 0);

    IMapper<TriggerMapper> mapper4 = getDataStore(context).getMapperFactory().getMapper(TriggerMapper.class);
    checkObserver_AllEvents(context, mapper4, 0);
  }

  /**
   * Defines an observer, which should be executed for two events, and only mapper SimpleMapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_OneMapper_TwoEvents(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);
    os.getMapperSettings().add(new ObserverMapperSettings(SimpleMapper.class.getName()));
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    os.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    settings.getObserverSettings().add(os);

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver(context, mapper, 1, ObserverEventType.AFTER_INSERT, ObserverEventType.BEFORE_INSERT);
    checkObserver(context, mapper, 0, ObserverEventType.AFTER_DELETE, ObserverEventType.AFTER_LOAD,
        ObserverEventType.BEFORE_DELETE, ObserverEventType.BEFORE_LOAD);

    IMapper<Person> mapper2 = getDataStore(context).getMapperFactory().getMapper(Person.class);
    checkObserver_AllEvents(context, mapper2, 0);
  }

  /**
   * Defines an observer, which should be executed for any event, but only mapper SimpleMapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_OneMapper_AllEvents(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);
    os.getMapperSettings().add(new ObserverMapperSettings(SimpleMapper.class.getName()));
    settings.getObserverSettings().add(os);

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver_AllEvents(context, mapper, 1);
    IMapper<Person> mapper2 = getDataStore(context).getMapperFactory().getMapper(Person.class);
    checkObserver_AllEvents(context, mapper2, 0);
  }

  /**
   * Defines an observer, which should be executed for any mapper for two events
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_TwoEvents(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    ObserverDefinition<TestObserver> os = new ObserverDefinition<>(TestObserver.class);
    os.getEventTypeList().add(ObserverEventType.AFTER_INSERT);
    os.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    settings.getObserverSettings().add(os);

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver(context, mapper, 1, ObserverEventType.AFTER_INSERT, ObserverEventType.BEFORE_INSERT);
    checkObserver(context, mapper, 0, ObserverEventType.AFTER_DELETE, ObserverEventType.AFTER_LOAD,
        ObserverEventType.BEFORE_DELETE, ObserverEventType.BEFORE_LOAD);

    IMapper<Person> mapper2 = getDataStore(context).getMapperFactory().getMapper(Person.class);
    checkObserver(context, mapper2, 1, ObserverEventType.AFTER_INSERT, ObserverEventType.BEFORE_INSERT);
    checkObserver(context, mapper2, 0, ObserverEventType.AFTER_DELETE, ObserverEventType.AFTER_LOAD,
        ObserverEventType.BEFORE_DELETE, ObserverEventType.BEFORE_LOAD);
  }

  /**
   * Defines an observer, which should be executed for any event and mapper
   * 
   * @param context
   */
  @Test
  public void testGlobalObserver_All(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    settings.getObserverSettings().add(new ObserverDefinition<>(TestObserver.class));

    IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    checkObserver_AllEvents(context, mapper, 1);
  }

  /**
   * Define an observer class, which will cause an instantiation exception ( no default constructor )
   * 
   * @param context
   */
  @Test
  public void testClassNotFound_ObserverClass(TestContext context) {
    DataStoreSettings settings = getDataStore(context).getSettings();
    settings.getObserverSettings().add(new ObserverDefinition<>(TestObserver_NoDefaultConstructor.class));
    try {
      IMapper<SimpleMapper> mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
      // we are expecting instantiation exception here
      mapper.getObserverHandler().getObserver(ObserverEventType.AFTER_DELETE);
      context.fail("expected exception here");
    } catch (Exception e) {
      // expected
      LOGGER.info(e);
      context.assertTrue(e.toString().contains("InstantiationException"), "expected InstantiationException");
    }
  }

}
