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

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.init.ObserverMapperSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningNoInterface;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWithInterface_V5;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWithInterface_V6;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWrongEvent;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.converter.V6Converter;
import de.braintags.vertx.jomnigate.versioning.IMapperVersion;
import de.braintags.vertx.jomnigate.versioning.SetMapperVersionObserver;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for the data versioning system
 * 
 * @author Michael Remme
 * 
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class TestDataVersioning extends DatastoreBaseTest {

  /**
   * check wether the {@link SetMapperVersionObserver} exists, cause it must be programmatically set by jomnigate
   * 
   * @param context
   */
  @Test
  public void testExisting_SetVersionObserver_Definition(TestContext context) {
    context.assertFalse(getDataStore(context).getSettings().getObserverSettings().isEmpty(),
        "No observer settings found");
    ObserverDefinition vs = getDataStore(context).getSettings().getObserverSettings()
        .getDefinition(SetMapperVersionObserver.class);
    context.assertNotNull(vs,
        "SetMapperVersionObserver is not existing and must be programmatically added by jomnigate itself");
    context.assertEquals(Integer.MAX_VALUE, vs.getPriority());
    context.assertFalse(vs.getMapperSettings().isEmpty(), "expcted valid mapper settings");
    context.assertEquals(1, vs.getEventTypeList().size(), "Expected one event type");
    context.assertTrue(vs.getEventTypeList().contains(ObserverEventType.BEFORE_INSERT),
        "expected event type BEFORE_SAVE");
    ObserverMapperSettings ms = (ObserverMapperSettings) vs.getMapperSettings().get(0);
    context.assertTrue(ms.isInstanceOf());
    context.assertEquals(IMapperVersion.class.getName(), ms.getClassDefinition());
  }

  @Test
  public void testMappingCorrect(TestContext context) {
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(VersioningWithInterface_V5.class);
    context.assertNotNull(mapper);
    Entity entity = mapper.getEntity();
    context.assertNotNull(mapper.getVersionInfo(), "expected VersionInfo in mapper");
    context.assertEquals(5l, mapper.getVersionInfo().version());
  }

  @Test
  public void testMappingNoInterface(TestContext context) {
    try {
      IMapper mapper = getDataStore(context).getMapperFactory().getMapper(VersioningNoInterface.class);
      context.fail("expected a MappingException here");
    } catch (MappingException e) {
      // expected result
    }
  }

  /**
   * Version conversion is only allowed at AFTER_LOAD and BEFORE_UPDATE
   * 
   * @param context
   */
  @Test
  public void testMappingWrongEventType(TestContext context) {
    try {
      IMapper mapper = getDataStore(context).getMapperFactory().getMapper(VersioningWrongEvent.class);
      context.fail("expected a MappingException here because of wrong event type");
    } catch (MappingException e) {
      // expected result
    }
  }

  /**
   * Save records with a defined version in entity definition and check for the version inside a stored record
   * 
   * @param context
   */
  @Test
  public void testSimpleVersioning(TestContext context) {
    clearTable(context, VersioningWithInterface_V5.class);
    VersioningWithInterface_V5 vi = new VersioningWithInterface_V5();
    saveRecord(context, vi);
    context.assertEquals(5l, vi.getMapperVersion(), "version was not automatically set");
    VersioningWithInterface_V5 vi2 = findRecordByID(context, VersioningWithInterface_V5.class, vi.id);
    context.assertEquals(5l, vi2.getMapperVersion(), "version was NOT saved");

    // set the version manually, save the record, it should NOT be updated
    vi2.setMapperVersion(88);
    saveRecord(context, vi2);
    context.assertEquals(88l, vi2.getMapperVersion(), "for an update the version should NOT be set");
    VersioningWithInterface_V5 vi3 = findRecordByID(context, VersioningWithInterface_V5.class, vi.id);
    context.assertEquals(88l, vi3.getMapperVersion(), "for an update the version should NOT be set");
  }

  /**
   * Save record as VersioningWithInterface_V5 with a defined version in entity definition and check for the version
   * inside a stored record.
   * Then load the same record, but as a new version, VersioningWithInterface_V6. Here the new field inside the record
   * must be empty.
   * Then save the record, afterwards the new field should be converted with a new value
   * 
   * @param context
   */
  @Test
  public void testVersioning_TwoVersions(TestContext context) {
    clearTable(context, VersioningWithInterface_V5.class);
    VersioningWithInterface_V5 vi = new VersioningWithInterface_V5();
    saveRecord(context, vi);
    context.assertEquals(5l, vi.getMapperVersion(), "version was not automatically set");
    VersioningWithInterface_V5 vi2 = findRecordByID(context, VersioningWithInterface_V5.class, vi.id);
    context.assertEquals(5l, vi2.getMapperVersion(), "version was NOT saved");

    // use the new version within a defined converter
    getDataStore(context).getMapperFactory().reset();
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(VersioningWithInterface_V6.class);
    context.assertNotNull(mapper.getVersionInfo(), "expected valid VersionInfo in mapper dclaration");
    context.assertTrue(mapper.getVersionInfo().versionConverter().length > 0, "Expected a defined VersionConverter");

    List<IObserver> osl = mapper.getObserverHandler().getObserver(ObserverEventType.BEFORE_UPDATE);
    context.assertFalse(osl.isEmpty(), "No definition for version conversion found");

    VersioningWithInterface_V6 vi3 = findRecordByID(context, VersioningWithInterface_V6.class, vi.id);
    context.assertEquals(5l, vi3.getMapperVersion(), "version was NOT saved");
    context.assertTrue(vi3.newName == null || vi3.newName.length() == 0, "newName must not be filled here");

    V6Converter.executed = false;
    // by savong the new record the value of the field "newName" must be filled because of VersionConverter
    saveRecord(context, vi3);
    context.assertTrue(V6Converter.executed, "V6Convefter was not executed");
    context.assertEquals(6l, vi3.getMapperVersion(), "mapper version not correctly raised");
    context.assertTrue(vi3.newName != null && vi3.newName.equals("converted Value V6"),
        "converter did not work; value is '" + vi3.newName);

    // check the saved instance
    VersioningWithInterface_V6 vi4 = findRecordByID(context, VersioningWithInterface_V6.class, vi.id);
    context.assertEquals(6l, vi4.getMapperVersion(), "mapper version not correctly raised");
    context.assertTrue(vi4.newName != null && vi4.newName.equals("converted Value V6"),
        "converter did not work; value is '" + vi3.newName);

  }

  @Test
  public void testVersioningEmbeddedMapper(TestContext context) {
    context.fail("unimplemented");
  }

  @Test
  public void testVersioningReferencedMapper(TestContext context) {
    context.fail("unimplemented");
  }

}
