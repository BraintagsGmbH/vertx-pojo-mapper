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

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.testdatastore.mapper.IPolyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper;
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
public class TestPolymorphMapper extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestPolymorphMapper.class);

  @Test
  public void testPolymorphism(TestContext context) {
    clearTable(context, "PolyMapper");

    PolyMapper polyMapper = new PolyMapper();
    polyMapper.setMainField("testMain1");

    testMapper(polyMapper, null, context);

    PolySubMapper polySubMapper = new PolySubMapper();
    polySubMapper.setMainField("testMain2");
    polySubMapper.setSubField("testSub");

    testMapper(polySubMapper, polyMapper, context);
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
