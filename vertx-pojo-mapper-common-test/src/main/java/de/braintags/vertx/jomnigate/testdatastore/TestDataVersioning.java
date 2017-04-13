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

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningNoInterface;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWithInterface;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for the data versioning system
 * 
 * @author Michael Remme
 * 
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class TestDataVersioning extends DatastoreBaseTest {

  @Test
  public void testMappingCorrect(TestContext context) {
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(VersioningWithInterface.class);
    context.assertNotNull(mapper);
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
    VersioningWithInterface vi = new VersioningWithInterface();
    saveRecord(context, vi);
    context.assertEquals(5l, vi.getMapperVersion(), "version was not automatically set");
    VersioningWithInterface vi2 = findRecordByID(context, VersioningWithInterface.class, vi.id);
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
