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

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import examples.mapper.DemoMapper;
import examples.mapper.DemoSubMapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

/**
 * Simple example to write and read Pojos
 * 
 * @author Michael Remme
 * 
 */

public class Examples {
  // -Djava.util.logging.config.file=/data/workspace/vertx/vertx-pojo-mapper/vertx-pojongo/src/main/resources/logging.properties
  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  /**
   * Init a MongoClient onto a locally running Mongo and the {@link MongoDataStore}
   * 
   * @param vertx
   */
  public void example1(Vertx vertx) {
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "PojongoTestDatabase");
    MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
    MongoDataStore mongoDataStore = new MongoDataStore(mongoClient, config);
  }

  /**
   * Create the object to be saved into the datastore
   */
  public void example2() {
    DemoMapper dm = new DemoMapper();
    dm.setName("demoMapper");
    DemoSubMapper dmsr = new DemoSubMapper();
    dmsr.subname = "referenced submapper";
    dm.subMapperReferenced = dmsr;

    DemoSubMapper dmse = new DemoSubMapper();
    dmse.subname = "embedded submapper";
    dm.subMapperEmbedded = dmse;
  }

  /**
   * Saving an instance intp the Datastore
   * 
   * @param mongoDataStore
   * @param dm
   */
  public void example3(MongoDataStore mongoDataStore, DemoMapper dm) {
    IWrite<DemoMapper> write = mongoDataStore.createWrite(DemoMapper.class);
    write.add(dm);
    write.save(result -> {
      if (result.failed()) {
        logger.error(result.cause());
      } else {
        IWriteResult wr = result.result();
        IWriteEntry entry = wr.iterator().next();
        logger.info("written with id " + entry.getId());
        logger.info("written action: " + entry.getAction());
        logger.info("written as " + entry.getStoreObject());
      }
    });
  }

  /**
   * Searching for objects
   * 
   * @param mongoDataStore
   */
  public void example4(MongoDataStore mongoDataStore) {
    IQuery<DemoMapper> query = mongoDataStore.createQuery(DemoMapper.class);
    query.field("name").is("demoMapper");
    query.execute(rResult -> {
      if (rResult.failed()) {
        logger.error(rResult.cause());
      } else {
        IQueryResult<DemoMapper> qr = rResult.result();
        qr.iterator().next(itResult -> {
          if (itResult.failed()) {
            logger.error(itResult.cause());
          } else {
            DemoMapper readMapper = itResult.result();
            logger.info("Query found id " + readMapper.id);
          }
        });
      }
    });
  }

  /**
   * Delete an instance from the Datastore
   * 
   * @param mongoDataStore
   * @param mapper
   */
  public void example5(MongoDataStore mongoDataStore, DemoMapper mapper) {
    IDelete<DemoMapper> delete = mongoDataStore.createDelete(DemoMapper.class);
    delete.add(mapper);
    delete.delete(deleteResult -> {
      if (deleteResult.failed()) {
        logger.error("", deleteResult.cause());
      } else {
        logger.info(deleteResult.result().getOriginalCommand());
      }
    });
  }

}
