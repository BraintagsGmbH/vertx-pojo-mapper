/*-
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
package de.braintags.vertx.jomnigate.dataaccess.query;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import io.vertx.core.Future;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class TestQueryInterator extends DatastoreBaseTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testIteratorDoesNotSwallowAllExceptions(TestContext context) {
    clearTable(context, MiniMapper.class);
    MiniMapper miniMapper = new MiniMapper("test");
    saveRecord(context, miniMapper);
    Future<MiniMapper> fut = Future.future();
    Async async = context.async();

    IQuery<MiniMapper> query = getDataStore(context).createQuery(MiniMapper.class);
    query.setSearchCondition(ISearchCondition.isEqual(MiniMapper.NAME, "test"));
    query.execute(result -> {
      if (result.succeeded()) {
        result.result().iterator().next(fut.completer());
      }

    });

    fut.setHandler(res -> {
      try {
        exception();
        context.fail("Exception expected");
        async.complete();
      } catch (IllegalArgumentException e) {
        async.complete();
      }

    });

    async.await();

  }

  private void exception() {
    throw new IllegalArgumentException();
  }

}
