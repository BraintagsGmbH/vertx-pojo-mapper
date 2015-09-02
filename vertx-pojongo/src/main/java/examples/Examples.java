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
package examples;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.test.core.VertxTestBase;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import examples.mapper.DemoMapper;
import examples.mapper.DemoSubMapper;
import examples.mapper.SimpleMapper;

/**
 * Simple example to write and read Pojos
 * 
 * @author Michael Remme
 * 
 */

public class Examples extends VertxTestBase {
  // -Djava.util.logging.config.file=/data/workspace/vertx/vertx-pojo-mapper/vertx-pojongo/src/main/resources/logging.properties
  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  private static MongoClient mongoClient;
  private IDataStore mongoDataStore;

  @Test
  public void Demo() {
    handleDemoMapper();
  }

  private void handleDemoMapper() {
    try {
      /*
       * Init a MongoClient onto a locally running Mongo
       */
      JsonObject config = new JsonObject();
      config.put("connection_string", "mongodb://localhost:27017");
      config.put("db_name", "PojongoTestDatabase");
      mongoClient = MongoClient.createNonShared(vertx, config);

      // Create a datastore
      mongoDataStore = new MongoDataStore(mongoClient);

      // Create the object wo be saved into the datastore
      DemoMapper dm = new DemoMapper();
      dm.setName("demoMapper");
      DemoSubMapper dmsr = new DemoSubMapper();
      dmsr.subname = "referenced submapper";
      dm.subMapperReferenced = dmsr;

      DemoSubMapper dmse = new DemoSubMapper();
      dmse.subname = "embedded submapper";
      dm.subMapperEmbedded = dmse;

      CountDownLatch latch = new CountDownLatch(1);

      IWrite<DemoMapper> write = mongoDataStore.createWrite(DemoMapper.class);
      write.add(dm);
      write.save(result -> {
        if (result.failed()) {
          logger.error(result.cause());
          fail(result.cause().getMessage());
          latch.countDown();
        } else {
          IWriteResult wr = result.result();
          IWriteEntry entry = wr.iterator().next();
          logger.info("written with id " + entry.getId());
          logger.info("written action: " + entry.getAction());
          logger.info("written as " + entry.getStoreObject());

          IQuery<DemoMapper> query = mongoDataStore.createQuery(DemoMapper.class);
          query.field("name").is("demoMapper");
          query.execute(rResult -> {
            if (rResult.failed()) {
              logger.error(rResult.cause());
              fail(rResult.cause().getMessage());
              latch.countDown();
            } else {
              IQueryResult<DemoMapper> qr = rResult.result();
              qr.iterator().next(itResult -> {
                if (itResult.failed()) {
                  logger.error(itResult.cause());
                  fail(itResult.cause().getMessage());
                  latch.countDown();
                } else {
                  DemoMapper readMapper = itResult.result();
                  logger.info("Query found id " + readMapper.id);

                  IDelete<DemoMapper> delete = mongoDataStore.createDelete(DemoMapper.class);
                  delete.add(readMapper);
                  delete.delete(deleteResult -> {
                    if (deleteResult.failed()) {
                      logger.error("", deleteResult.cause());
                      fail(deleteResult.cause().getMessage());
                      latch.countDown();
                    } else {
                      logger.info(deleteResult.result().getOriginalCommand());
                      latch.countDown();
                    }
                  });

                }
              });
            }
          });

        }
      });

      try {
        latch.await();
      } catch (InterruptedException e) {
        logger.error("", e);
      }

    } finally {
      if (mongoClient != null)
        mongoClient.close();
    }

  }

  private void saveSimpleMapper() {
    try {
      /*
       * Init a MongoClient onto a locally running Mongo
       */
      JsonObject config = new JsonObject();
      config.put("connection_string", "mongodb://localhost:27017");
      config.put("db_name", "PojongoTestDatabase");
      mongoClient = MongoClient.createNonShared(vertx, config);

      // Create a datastore
      mongoDataStore = new MongoDataStore(mongoClient);

      // Create the object wo be saved into the datastore
      SimpleMapper dm = new SimpleMapper();
      dm.setName("demoMapper2");

      IMapper mapper = mongoDataStore.getMapperFactory().getMapper(DemoMapper.class);
      IWrite<SimpleMapper> write = mongoDataStore.createWrite(SimpleMapper.class);
      write.add(dm);
      CountDownLatch latch = new CountDownLatch(1);
      write.save(result -> {
        if (result.failed()) {
          logger.error(result.cause());
          fail(result.cause().getMessage());
          latch.countDown();
        } else {
          IWriteResult wr = result.result();
          IWriteEntry entry = wr.iterator().next();
          logger.info("written with id " + entry.getId());
          logger.info("written action: " + entry.getAction());
          logger.info("written as " + entry.getStoreObject());

          IQuery<SimpleMapper> query = mongoDataStore.createQuery(SimpleMapper.class);
          query.field("name").is("demoMapper");
          query.execute(rResult -> {
            if (rResult.failed()) {
              logger.error(rResult.cause());
              fail(rResult.cause().getMessage());
              latch.countDown();
            } else {
              IQueryResult<SimpleMapper> qr = rResult.result();
              qr.iterator().next(itResult -> {
                if (itResult.failed()) {
                  logger.error(itResult.cause());
                  fail(itResult.cause().getMessage());
                  latch.countDown();
                } else {
                  SimpleMapper readMapper = itResult.result();
                  logger.info("Query found id " + readMapper.id);
                  latch.countDown();
                }
              });
            }
          });

        }
      });

      try {
        latch.await();
      } catch (InterruptedException e) {
        logger.error("", e);
      }

    } finally {
      if (mongoClient != null)
        mongoClient.close();
    }

  }

}
