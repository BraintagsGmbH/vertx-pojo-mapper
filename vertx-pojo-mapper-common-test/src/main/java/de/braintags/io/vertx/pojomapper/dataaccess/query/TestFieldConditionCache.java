/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.VariableFieldCondition;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TestFieldConditionCache extends DatastoreBaseTest {

  /**
   * Creates a query that is executed two times.
   * For the fixed condition, the second time should use the intermediate result from the cache.
   * For the variable condition, the result should be created anew both times
   *
   * @param context
   */
  @Test
  public void testQuerySearchConditionCache(TestContext context) {
    clearTable(context, MiniMapper.class);
    MiniMapper miniMapper = new MiniMapper("test");
    saveRecord(context, miniMapper);
    Async async = context.async();

    IQuery<MiniMapper> query = getDataStore(context).createQuery(MiniMapper.class);
    TestFieldCondition fixedCondition = new TestFieldCondition("name", QueryOperator.EQUALS, "test");
    TestVariableFieldCondition variableCondition = new TestVariableFieldCondition("name", QueryOperator.EQUALS,
        "${variable}");
    query.setSearchCondition(query.and(fixedCondition, variableCondition));
    final AtomicInteger variableResolved = new AtomicInteger(0);
    query.execute(resolve -> {
      variableResolved.incrementAndGet();
      return "test";
    }, 10, 0, result1 -> {
      context.assertTrue(result1.succeeded());
      context.assertEquals(1, result1.result().size());
      query.execute(resolve -> {
        variableResolved.incrementAndGet();
        return "test";
      }, 10, 0, result2 -> {
        context.assertTrue(result2.succeeded());
        context.assertEquals(1, result2.result().size());
        // the transformation must have been called both times
        context.assertEquals(2, variableResolved.get());
        // fixed condition should cache the result the 1st time, and return it the 2nd time
        context.assertEquals(1, fixedCondition.cachedResultReturned);
        context.assertEquals(1, fixedCondition.resultCached);
        async.complete();
      });
    });
  }

  /**
   * Extension of field condition to check the cache access
   */
  private class TestFieldCondition extends FieldCondition {

    private int cachedResultReturned = 0;
    private int resultCached = 0;

    public TestFieldCondition(String field, QueryOperator logic, @Nullable Object value) {
      super(field, logic, value);
    }

    @Override
    public Object getIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass) {
      Object intermediateResult = super.getIntermediateResult(queryExpressionClass);
      if (intermediateResult != null)
        cachedResultReturned++;
      return intermediateResult;
    }

    @Override
    public void setIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass, Object result) {
      resultCached++;
      super.setIntermediateResult(queryExpressionClass, result);
    }
  }

  /**
   * Extension of variable field condition to check cache result
   */
  private class TestVariableFieldCondition extends VariableFieldCondition {
    public TestVariableFieldCondition(String field, QueryOperator logic, @Nullable Object value) {
      super(field, logic, value);
    }

    @Override
    public Object getIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass) {
      Object intermediateResult = super.getIntermediateResult(queryExpressionClass);
      Assert.assertNull(intermediateResult);
      return intermediateResult;
    }
  }
}
