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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.testdatastore.mapper.IPolyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper_WithoutPolyClass;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper_WithoutType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolySubMapper;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * Test for polymorphism in mappers
 * 
 * @author sschmitt
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class TestPolymorphMapper extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestPolymorphMapper.class);

  @Test
  public void testPolymorphism(TestContext context) {
    clearTable(context, "PolyMapper");

    PolyMapper polyMapper = new PolyMapper();
    polyMapper.setMainField("testMain1");
    Object polyMapperId = saveRecord(context, polyMapper).writeResult.iterator().next().getId();

    PolySubMapper polySubMapper = new PolySubMapper();
    polySubMapper.setMainField("testMain2");
    polySubMapper.setSubField("testSub");
    Object polySubMapperId = saveRecord(context, polySubMapper).writeResult.iterator().next().getId();

    // query must give 2 records in PolyMapper
    IQuery<PolyMapper> query = getDataStore(context).createQuery(PolyMapper.class);
    List pList = findAll(context, query);
    context.assertEquals(2, pList.size(), "expected 2 records");

    // query must give 2 records in PolyMapper either
    query = getDataStore(context).createQuery(PolySubMapper.class);
    pList = findAll(context, query);
    context.assertEquals(2, pList.size(), "expected 2 records");

    query = getDataStore(context).createQuery(PolySubMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual("subField", "testSub"));
    pList = findAll(context, query);
    context.assertEquals(1, pList.size(), "expected 1 records");

    query = getDataStore(context).createQuery(PolyMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual("mainField", "testMain1"));
    pList = findAll(context, query);
    context.assertEquals(1, pList.size(), "expected 1 records");

  }

  @Test
  public void testPolymorphismSaveMixedList(TestContext context) {
    clearTable(context, "PolyMapper");
    List recs = new ArrayList<>();
    PolyMapper polyMapper = new PolyMapper();
    polyMapper.setMainField("testMain1");
    recs.add(polyMapper);

    PolySubMapper polySubMapper = new PolySubMapper();
    polySubMapper.setMainField("testMain2");
    polySubMapper.setSubField("testSub");
    recs.add(polySubMapper);
    saveRecords(context, recs);

    // query must give 2 records in PolyMapper
    IQuery<PolyMapper> query = getDataStore(context).createQuery(PolyMapper.class);
    List pList = findAll(context, query);
    context.assertEquals(2, pList.size(), "expected 2 records");

    // query must give 2 records in PolyMapper either
    query = getDataStore(context).createQuery(PolySubMapper.class);
    pList = findAll(context, query);
    context.assertEquals(2, pList.size(), "expected 2 records");
  }

  /**
   * If the annotation JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property =
   * "@class")
   * is set, then {@link Entity#polyClass()} must exist either
   * 
   * @param context
   */
  @Test
  public void testCheckUndefinedPolyClass(TestContext context) {
    try {
      IQuery<PolyMapper_WithoutPolyClass> query = getDataStore(context).createQuery(PolyMapper_WithoutPolyClass.class);
      context.fail("expected MappingException here");
    } catch (MappingException e) {
      // expected result
    }

  }

  /**
   * If the annotation {@link Entity#polyClass()}, then the annotation JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
   * include = JsonTypeInfo.As.PROPERTY, property = "@class")
   * must exist either
   * 
   * @param context
   */
  @Test
  public void testCheckUndefined_JsonTypeInfo(TestContext context) {
    try {
      IQuery<PolyMapper> query = getDataStore(context).createQuery(PolyMapper_WithoutType.class);
      context.fail("expected MappingException here");
    } catch (MappingException e) {
      // expected result
    }
  }

  @Test
  public void testDeserialization(TestContext context) throws JsonProcessingException {
    clearTable(context, "PolyMapper");
    PolyMapper polyMapper = new PolyMapper();
    polyMapper.setMainField("testMain1");

    PolySubMapper polySubMapper = new PolySubMapper();
    polySubMapper.setMainField("testMain2");
    polySubMapper.setSubField("testSub");

    JsonNode polyTree = Json.mapper.valueToTree(polyMapper);
    JsonNode polySubTree = Json.mapper.valueToTree(polySubMapper);
    IPolyMapper sub2 = Json.mapper.treeToValue(polySubTree, IPolyMapper.class);
    IPolyMapper poly2 = Json.mapper.treeToValue(polyTree, IPolyMapper.class);
    assertThat(sub2, instanceOf(PolySubMapper.class));
    assertThat(poly2, instanceOf(PolyMapper.class));
  }

  private void testMapper(PolyMapper polyMapper, PolyMapper otherPolyMapper, TestContext context) {
    ResultContainer resultContainer = saveRecord(context, polyMapper);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(WriteAction.INSERT, we.getAction());
    context.assertNotNull(we.getStoreObject());
    context.assertNotNull(polyMapper.getId());
    logger.debug("Saved record is " + polyMapper.toString());

    if (otherPolyMapper != null)
      context.assertNotEquals(polyMapper.getId(), otherPolyMapper.getId(),
          "Records should be in the same collection and thus should not have the same ID");

    IQuery<PolyMapper> query = getDataStore(context).createQuery(PolyMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual(query.getMapper().getIdField().getName(), polyMapper.getId()));
    resultContainer = find(context, query, 1);
    Async async = context.async();
    resultContainer.queryResult.iterator().next(context.asyncAssertSuccess(loadedPolyMapper -> {
      context.assertEquals(loadedPolyMapper, polyMapper);
      async.complete();
    }));
    async.await();
  }

}
