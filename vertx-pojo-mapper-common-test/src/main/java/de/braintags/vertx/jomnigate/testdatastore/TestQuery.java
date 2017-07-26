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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedField;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.testdatastore.mapper.DeepRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EnumRecord;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 *
 *
 * @author Michael Remme
 *
 */
public class TestQuery extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestQuery.class);
  private static boolean dropTable = false;

  @Test
  public void testDeepQuery(TestContext context) {
    super.clearTable(context, DeepRecord.class.getSimpleName());
    List<DeepRecord> childList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      childList.add(new DeepRecord("Record " + i));
    }
    saveRecords(context, childList);

    IQuery<DeepRecord> query0 = getDataStore(context).createQuery(DeepRecord.class);
    IndexedField recordField = new IndexedField("name");
    query0.setSearchCondition(ISearchCondition.isEqual(recordField, "Record 1"));
    find(context, query0, 1);

    IQuery<DeepRecord> query = getDataStore(context).createQuery(DeepRecord.class);
    IndexedField deepChildField = new IndexedField("child.name");
    query.setSearchCondition(ISearchCondition.isEqual(deepChildField, "child Record 1"));
    find(context, query, 1);

    IQuery<DeepRecord> query2 = getDataStore(context).createQuery(DeepRecord.class);
    IndexedField deeperChildField = new IndexedField("child.deeperChild.name");
    query.setSearchCondition(ISearchCondition.isEqual(deeperChildField, "deeper child Record 1"));
    find(context, query, 1);

  }

  /**
   * Search:
   */
  @Test
  public void testNot(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("erste", "zweite");
    query.setSearchCondition(ISearchCondition.not(ISearchCondition.in(SimpleMapper.SECOND_PROPERTY, it)));
    find(context, query, 6);
  }

  @Test
  public void testSimpleOr(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"));
    find(context, query, 2);

    query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.or(ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "erste"),
        ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "zweite")));
    find(context, query, 2);
  }

  @Test
  public void testIs_String(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual("intValue", 10));
    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
  }

  @Test
  public void testIs_UnknownField(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    try {
      query.setSearchCondition(ISearchCondition.isEqual("XXXXXXX", "Dublette"));
      context.fail("expected NoSuchFieldException");
    } catch (NoSuchFieldException e) {
      // expected result
    }
  }

  @Test
  public void testIs_Id(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    SimpleMapper sm = (SimpleMapper) findFirst(context, query);
    query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual("id", sm.id));

    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
  }

  @Test
  public void testIs(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"));
    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
  }

  @Test
  public void testSimpleAnd(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"),
        ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "erste")));
    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
  }

  @Test
  public void testSimpleAndCount(TestContext context) {
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"),
        ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "erste")));
    findCount(context, query, 1);
  }

  @Test
  public void testQueryMultipleFields(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"));
    find(context, query, 2);

    query.setSearchCondition(ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "Dublette"),
        ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "erste")));
    find(context, query, 1);
  }

  /**
   * Search: Name = "AndOr" AND secondProperty="AndOr 1" OR secondProperty="AndOr 2"
   *
   */
  @Test
  public void testAndOr(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "AndOr"),
        ISearchCondition.or(ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "AndOr 1"),
            ISearchCondition.isEqual(SimpleMapper.SECOND_PROPERTY, "AndOr 2"))));
    find(context, query, -1);
  }

  /**
   * Search:
   */
  @Test
  public void testIn(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("Dublette", "AndOr");
    query.setSearchCondition(ISearchCondition.in(SimpleMapper.NAME, it));
    find(context, query, 5);
  }

  /**
   * Search:
   */
  @Test
  public void testNotIn(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("erste", "zweite");
    query.setSearchCondition(ISearchCondition.notIn(SimpleMapper.SECOND_PROPERTY, it));
    find(context, query, 6);
  }

  /**
   * Search:
   */
  @Test
  public void testIsNot(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.notEqual(SimpleMapper.NAME, "Dublette"));
    find(context, query, 6);
  }

  /**
   * Search:
   */
  @Test
  public void testContains(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "ab"));
    find(context, query, 3);
  }

  /**
   * Search:
   */
  @Test
  public void testContainsCaseInsensitive(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "AB"));

    find(context, query, 3);
  }

  /**
   * Search:
   */
  @Test
  public void testStartsWith(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "aa"));

    find(context, query, 3);
  }

  @Test
  public void testEndsWith(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "cc"));

    find(context, query, 3);
  }

  @Test
  public void testFindLimit(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "cc"));
    ResultContainer resultContainer = find(context, query, 2, 2);
    context.assertEquals(2, resultContainer.queryResult.size());
    context.assertEquals((long) -1, resultContainer.queryResult.getCompleteResult());
  }

  @Test
  public void testFindLimitWithoutQueryArgs(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    ResultContainer resultContainer = find(context, query, 2, 2);
    context.assertEquals(2, resultContainer.queryResult.size());
    context.assertEquals((long) -1, resultContainer.queryResult.getCompleteResult());
  }

  @Test
  public void testFindLimitGetCompleteCount(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "cc"));
    query.setReturnCompleteCount(true);

    ResultContainer resultContainer = find(context, query, 2, 2);
    context.assertEquals((long) 3, resultContainer.queryResult.getCompleteResult());
  }

  @Test
  public void testFindLimitGetCompleteCountQueryStart(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "cc"));
    query.setReturnCompleteCount(true);

    ResultContainer resultContainer = find(context, query, 1, 2, 2);
    context.assertEquals((long) 3, resultContainer.queryResult.getCompleteResult(), "incorrect complete result");
  }

  @Test
  public void testFindSorted(TestContext context) {
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    query.setSearchCondition(ISearchCondition.contains(SimpleMapper.SECOND_PROPERTY, "e"));
    query.addSort("secondProperty", false);
    List<SimpleMapper> list = findAll(context, query);
    context.assertEquals(2, list.size(), "incorrect result");
    context.assertEquals(list.get(0).getSecondProperty(), "zweite", "sorting does not work");
    list.forEach(sm -> logger.info(sm.getSecondProperty()));
  }

  @Test
  public void testFindByEnum(TestContext context) {
    createDemoRecords(context);
    IQuery<EnumRecord> query = getDataStore(context).createQuery(EnumRecord.class);
    query.setSearchCondition(ISearchCondition.isEqual(EnumRecord.ENUM_ENUM, WriteAction.INSERT));
    List<EnumRecord> list = findAll(context, query);
    list.forEach(sm -> logger.info("Found enum: " + sm.enumEnum));
    context.assertEquals(1, list.size(), "incorrect result");
  }

  @Test
  public void testFindByEnumContains(TestContext context) {
    createDemoRecords(context);
    IQuery<EnumRecord> query = getDataStore(context).createQuery(EnumRecord.class);
    query.setSearchCondition(ISearchCondition.contains(EnumRecord.ENUM_ENUM, "INSER"));
    List<EnumRecord> list = findAll(context, query);
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
      saveRecord(context, sm);

      sm = new SimpleMapper();
      sm.name = "Dublette";
      sm.setSecondProperty("zweite");
      sm.intValue = 11;
      saveRecord(context, sm);

      for (int i = 0; i < 3; i++) {
        sm = new SimpleMapper();
        sm.name = "AndOr";
        sm.setSecondProperty("AndOr " + i);
        sm.intValue = i + 1;
        saveRecord(context, sm);
      }

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      saveRecord(context, sm);

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      saveRecord(context, sm);

      sm = new SimpleMapper();
      sm.name = "startswith";
      sm.setSecondProperty("aabbcc");
      sm.intValue = 11;
      saveRecord(context, sm);

      EnumRecord en = new EnumRecord();
      en.enumEnum = WriteAction.INSERT;
      saveRecord(context, en);

      en = new EnumRecord();
      en.enumEnum = WriteAction.UNKNOWN;
      saveRecord(context, en);

      en = new EnumRecord();
      en.enumEnum = WriteAction.UPDATE;
      saveRecord(context, en);

    }

  }

}
