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

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.init.ObserverMapperSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningNoInterface;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWithInterface_V5;
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
    context.assertEquals(5l, entity.version());
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

  @Test
  public void testVersioning(TestContext context) {
    clearTable(context, VersioningWithInterface_V5.class);
    VersioningWithInterface_V5 vi = new VersioningWithInterface_V5();
    saveRecord(context, vi);
    context.assertEquals(5l, vi.getMapperVersion(), "version was not automatically set");
    VersioningWithInterface_V5 vi2 = findRecordByID(context, VersioningWithInterface_V5.class, vi.id);
    context.assertEquals(5l, vi2.getMapperVersion(), "version was saved");

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
