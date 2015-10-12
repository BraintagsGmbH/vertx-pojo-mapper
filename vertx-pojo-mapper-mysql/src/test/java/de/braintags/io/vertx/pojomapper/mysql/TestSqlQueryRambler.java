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

package de.braintags.io.vertx.pojomapper.mysql;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.MiniMapper;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQuery;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQueryRambler;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;

/**
 * testing of {@link SqlQueryRambler}
 * 
 * @author Michael Remme
 * 
 */

public class TestSqlQueryRambler extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestSqlQueryRambler.class);

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckInterval(10000);
    options.setWarningExceptionTime(10000);
    return options;
  }

  @Test
  public void simpleEquals() {
    CountDownLatch latch = new CountDownLatch(1);
    SqlQuery<MiniMapper> query = (SqlQuery<MiniMapper>) getDataStore().createQuery(MiniMapper.class);
    query.field("name").is("name to find");
    SqlQueryRambler rambler = new SqlQueryRambler();
    query.executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        latch.countDown();
      } else {
        String statement = rambler.getQueryStatement();
        JsonArray parameter = rambler.getQueryParameters();
        LOGGER.info(statement);
        LOGGER.info(parameter);
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      LOGGER.error("", e);
    } finally {
      testComplete();
    }

  }

}
// {"name":{"=":"looper"}}
// {"name":{"$eq":"Dublette"},"secondProperty":{"$eq":"erste"}}
// {"$and":[{"name":{"$eq":"Dublette"}},{"secondProperty":{"$eq":"erste"}}]}
// {"secondProperty":{"$nin":["erste","zweite"]}}
// {"$and":[{"name":{"$eq":"AndOr"}},{"$or":[{"secondProperty":{"$eq":"AndOr 1"}},{"secondProperty":{"$eq":"AndOr
// 2"}}]}]}
