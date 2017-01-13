/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package examples;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit;
import de.braintags.io.vertx.util.exception.InitException;
import examples.mapper.DemoMapper;
import examples.mapper.DemoSubMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.FileSystemException;
import io.vertx.core.json.Json;
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
  private Vertx vertx;

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
    new MongoDataStore(vertx, mongoClient, config);
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
    query.setSearchCondition(query.isEqual("name", "demoMapper"));
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

  /**
   * Init a datastore by using DataStoreSettings
   */
  public void example6(Handler<AsyncResult<IDataStore>> handler) {
    try {
      DataStoreSettings settings = loadDataStoreSettings("/some/path/to/settings.json");
      IDataStoreInit dsInit = settings.getDatastoreInit().newInstance();
      dsInit.initDataStore(vertx, settings, initResult -> {
        if (initResult.failed()) {
          logger.error("could not start mongo client", initResult.cause());
          handler.handle(Future.failedFuture(new InitException(initResult.cause())));
        } else {
          handler.handle(Future.succeededFuture(initResult.result()));
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  public DataStoreSettings loadDataStoreSettings(String path) {
    FileSystem fs = vertx.fileSystem();
    if (fs.existsBlocking(path)) {
      Buffer buffer = fs.readFileBlocking(path);
      DataStoreSettings settings = Json.decodeValue(buffer.toString(), DataStoreSettings.class);
      return settings;
    } else {
      DataStoreSettings settings = MongoDataStoreInit.createDefaultSettings();
      fs.writeFileBlocking(path, Buffer.buffer(Json.encode(settings)));
      throw new FileSystemException("File did not exist and was created new in path " + path);
    }
  }

  public void executeNative(IDataStore datastore) {
    MongoClient client = (MongoClient) datastore.getClient();
    JsonObject insertCommand = new JsonObject();
    insertCommand.put("name", "testName");
    client.insert("TestCollection", insertCommand, result -> {
      if (result.failed()) {
        logger.error("", result.cause());
      } else {
        logger.info("executed: " + result.result());
      }
    });
  }

}
