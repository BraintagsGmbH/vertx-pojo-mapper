/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.EnumRecord;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestSimpleMapperQuery extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestSimpleMapperQuery.class);
  private static boolean dropTable = false;

  @Test
  public void testSimpleOr(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query = getDataStore(context).createQuery(SimpleMapper.class);
    query.or("secondProperty").is("erste").field("secondProperty").is("zweite");
    resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testSimpleAnd(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.and("name").is("Dublette").field("secondProperty").is("erste");
    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testSimpleAndCount(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.and("name").is("Dublette").field("secondProperty").is("erste");
    ResultContainer resultContainer = findCount(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testQueryMultipleFields(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query.field("secondProperty").is("erste");
    resultContainer = find(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search: Name = "AndOr" AND secondProperty="AndOr 1" OR secondProperty="AndOr 2"
   * 
   */
  @Test
  public void testAndOr(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.and("name").is("AndOr").field("secondProperty").is("AndOr 1").or("secondProperty").is("AndOr 2");

    ResultContainer resultContainer = find(context, query, -1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIn(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("Dublette", "AndOr");
    query.field("name").in(it);

    ResultContainer resultContainer = find(context, query, 5);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testNotIn(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("erste", "zweite");
    query.field("secondProperty").notIn(it);

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIsNot(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("name").isNot("Dublette");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testContains(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("ab");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testContainsCaseInsensitive(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("AB");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testStartsWith(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("aa");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testEndsWith(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("cc");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testFindLimit(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("cc");
    query.setLimit(2);

    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    context.assertEquals(2, resultContainer.queryResult.size());
    context.assertEquals((long) -1, resultContainer.queryResult.getCompleteResult());
  }

  @Test
  public void testFindLimitGetCompleteCount(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("cc");
    query.setLimit(2);
    query.setReturnCompleteCount(true);

    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    context.assertEquals((long) 3, resultContainer.queryResult.getCompleteResult());
  }

  @Test
  public void testFindLimitGetCompleteCountQueryStart(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("cc");
    query.setLimit(2);
    query.setStart(2);
    query.setReturnCompleteCount(true);

    ResultContainer resultContainer = find(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    context.assertEquals((long) 3, resultContainer.queryResult.getCompleteResult(), "incorrect complete result");
  }

  @Test
  public void testFindSorted(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.field("secondProperty").contains("e");
    query.addSort("secondProperty", false);
    List<SimpleMapper> list = (List<SimpleMapper>) findAll(context, query);
    context.assertEquals(2, list.size(), "incorrect result");
    context.assertEquals(list.get(0).getSecondProperty(), "zweite", "sorting does not work");
    list.forEach(sm -> logger.info(sm.getSecondProperty()));
  }

  @Test
  public void testFindByEnum(TestContext context) {
    createDemoRecords(context);
    IQuery<EnumRecord> query = getDataStore(context).createQuery(EnumRecord.class);
    query.field("enumEnum").is(WriteAction.INSERT);
    List<EnumRecord> list = (List<EnumRecord>) findAll(context, query);
    list.forEach(sm -> logger.info(sm.enumEnum));
    context.assertEquals(1, list.size(), "incorrect result");
  }

  @Test
  public void testFindByEnumContains(TestContext context) {
    createDemoRecords(context);
    IQuery<EnumRecord> query = getDataStore(context).createQuery(EnumRecord.class);
    query.field("enumEnum").contains("IN");
    List<EnumRecord> list = (List<EnumRecord>) findAll(context, query);
    list.forEach(sm -> logger.info(sm.enumEnum));
    context.assertEquals(1, list.size(), "incorrect result");
  }

  /*
   * **************************************************** Helper Part
   */

  private void createDemoRecords(TestContext context) {
    if (!dropTable) {
      dropTable = true;
      super.clearTable(context, "SimpleMapper");
      super.clearTable(context, "EnumRecord");

      SimpleMapper sm = new SimpleMapper();
      sm.name = "Dublette";
      sm.setSecondProperty("erste");
      sm.intValue = 10;
      ResultContainer resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      sm = new SimpleMapper();
      sm.name = "Dublette";
      sm.setSecondProperty("zweite");
      sm.intValue = 11;
      resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      for (int i = 0; i < 3; i++) {
        sm = new SimpleMapper();
        sm.name = "AndOr";
        sm.setSecondProperty("AndOr " + i);
        sm.intValue = i + 1;
        resultContainer = saveRecord(context, sm);
        if (resultContainer.assertionError != null)
          throw resultContainer.assertionError;
      }

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      EnumRecord en = new EnumRecord();
      en.enumEnum = WriteAction.INSERT;
      resultContainer = saveRecord(context, en);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      en = new EnumRecord();
      en.enumEnum = WriteAction.UNKNOWN;
      resultContainer = saveRecord(context, en);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

      en = new EnumRecord();
      en.enumEnum = WriteAction.UPDATE;
      resultContainer = saveRecord(context, en);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;

    }

  }

}
