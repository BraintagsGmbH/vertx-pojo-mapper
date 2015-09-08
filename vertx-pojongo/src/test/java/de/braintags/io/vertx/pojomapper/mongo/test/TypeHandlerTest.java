/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.test.MongoBaseTest;
import de.braintags.io.vertx.pojomapper.test.ResultContainer;
import de.braintags.io.vertx.pojomapper.test.TypeHandlerTest;
import de.braintags.io.vertx.pojomapper.test.mapper.EmbeddedMapper_Array;
import de.braintags.io.vertx.pojomapper.test.mapper.EmbeddedMapper_List;
import de.braintags.io.vertx.pojomapper.test.mapper.EmbeddedMapper_Map;
import de.braintags.io.vertx.pojomapper.test.mapper.EmbeddedMapper_Single;
import de.braintags.io.vertx.pojomapper.test.mapper.ReferenceMapper_Array;
import de.braintags.io.vertx.pojomapper.test.mapper.ReferenceMapper_List;
import de.braintags.io.vertx.pojomapper.test.mapper.ReferenceMapper_Map;
import de.braintags.io.vertx.pojomapper.test.mapper.ReferenceMapper_Single;
import de.braintags.io.vertx.pojomapper.test.mapper.SimpleMapper;
import de.braintags.io.vertx.pojomapper.test.mapper.TypehandlerTestMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Test the {@link ITypeHandler} used for mongo with a mapper which contains all datatypes to be checked
 * 
 * @author Michael Remme
 * 
 */

public class TypeHandlerTest extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TypeHandlerTest.class);

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");
    MongoBaseTest.startMongo();
  }

  @AfterClass
  public static void afterClass() {
    MongoBaseTest.stopMongo();
  }

  @Test
  public void testSaveAndRead_TypehandlerTestMapper() {
    TypehandlerTestMapper sm = new TypehandlerTestMapper();
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<TypehandlerTestMapper> query = getDataStore().createQuery(TypehandlerTestMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        assertTrue(sm.equals(result.result()));
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_ReferenceMapperSingle() {
    SimpleMapper sc = new SimpleMapper();
    sc.name = "name";
    sc.setSecondProperty("2. property");

    ReferenceMapper_Single om = new ReferenceMapper_Single();
    om.simpleMapper = sc;

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<ReferenceMapper_Single> query = getDataStore().createQuery(ReferenceMapper_Single.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        ReferenceMapper_Single oom = (ReferenceMapper_Single) result.result();
        assertNotNull(oom.simpleMapper);
        assertTrue(oom.simpleMapper.id != null && !oom.simpleMapper.id.isEmpty());
        assertEquals(om.simpleMapper.name, oom.simpleMapper.name);
        assertEquals(om.simpleMapper.getSecondProperty(), oom.simpleMapper.getSecondProperty());
        checkAnnotations(oom.simpleMapper);
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_ReferenceMapperArray() {

    ReferenceMapper_Array om = new ReferenceMapper_Array();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<ReferenceMapper_Array> query = getDataStore().createQuery(ReferenceMapper_Array.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        ReferenceMapper_Array oom = (ReferenceMapper_Array) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.length; i++) {
          SimpleMapper omSm = om.simpleMapper[i];
          SimpleMapper oomSm = oom.simpleMapper[i];

          assertTrue(oomSm.id != null && !oomSm.id.isEmpty());
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_ReferenceMapperList() {

    ReferenceMapper_List om = new ReferenceMapper_List();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<ReferenceMapper_List> query = getDataStore().createQuery(ReferenceMapper_List.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        ReferenceMapper_List oom = (ReferenceMapper_List) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.size(); i++) {
          SimpleMapper omSm = om.simpleMapper.get(i);
          SimpleMapper oomSm = oom.simpleMapper.get(i);

          assertTrue(oomSm.id != null && !oomSm.id.isEmpty());
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_ReferenceMapperMap() {

    ReferenceMapper_Map om = new ReferenceMapper_Map();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<ReferenceMapper_Map> query = getDataStore().createQuery(ReferenceMapper_Map.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        ReferenceMapper_Map oom = (ReferenceMapper_Map) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.size(); i++) {
          SimpleMapper omSm = om.simpleMapper.get(i);
          SimpleMapper oomSm = oom.simpleMapper.get(i);

          assertTrue(oomSm.id != null && !oomSm.id.isEmpty());
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_EmbeddedMapperSingle() {
    SimpleMapper sc = new SimpleMapper();
    sc.name = "name";
    sc.setSecondProperty("2. property");

    EmbeddedMapper_Single om = new EmbeddedMapper_Single();
    om.simpleMapper = sc;

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<EmbeddedMapper_Single> query = getDataStore().createQuery(EmbeddedMapper_Single.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        EmbeddedMapper_Single oom = (EmbeddedMapper_Single) result.result();
        assertNotNull(oom.simpleMapper);
        assertTrue(oom.simpleMapper.id == null);
        assertEquals(om.simpleMapper.name, oom.simpleMapper.name);
        assertEquals(om.simpleMapper.getSecondProperty(), oom.simpleMapper.getSecondProperty());
        checkAnnotations(oom.simpleMapper);
        logger.info("finished!");
      }
    });
  }

  private void checkAnnotations(SimpleMapper rsm) {
    assertEquals("succeeded", rsm.afterLoad);
    assertEquals("succeeded", rsm.beforeSave);
  }

  @Test
  public void testSaveAndRead_EmbeddedMapperArray() {

    EmbeddedMapper_Array om = new EmbeddedMapper_Array();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<EmbeddedMapper_Array> query = getDataStore().createQuery(EmbeddedMapper_Array.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        EmbeddedMapper_Array oom = (EmbeddedMapper_Array) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.length; i++) {
          SimpleMapper omSm = om.simpleMapper[i];
          SimpleMapper oomSm = oom.simpleMapper[i];

          assertTrue(oomSm.id == null);
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_EmbeddedMapperList() {

    EmbeddedMapper_List om = new EmbeddedMapper_List();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<EmbeddedMapper_List> query = getDataStore().createQuery(EmbeddedMapper_List.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        EmbeddedMapper_List oom = (EmbeddedMapper_List) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.size(); i++) {
          SimpleMapper omSm = om.simpleMapper.get(i);
          SimpleMapper oomSm = oom.simpleMapper.get(i);

          assertTrue(oomSm.id == null);
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_EmbeddedMapperMap() {

    EmbeddedMapper_Map om = new EmbeddedMapper_Map();

    ResultContainer resultContainer = saveRecord(om);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<EmbeddedMapper_Map> query = getDataStore().createQuery(EmbeddedMapper_Map.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        EmbeddedMapper_Map oom = (EmbeddedMapper_Map) result.result();
        assertNotNull(oom.simpleMapper);
        for (int i = 0; i < om.simpleMapper.size(); i++) {
          SimpleMapper omSm = om.simpleMapper.get(i);
          SimpleMapper oomSm = oom.simpleMapper.get(i);

          assertTrue(oomSm.id == null);
          assertEquals(omSm.name, oomSm.name);
          assertEquals(omSm.getSecondProperty(), oomSm.getSecondProperty());
        }
        logger.info("finished!");
      }
    });
  }

}
