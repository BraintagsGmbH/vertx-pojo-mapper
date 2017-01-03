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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.util.exception.InitException;
import examples.mapper.MiniMapper;
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
import io.vertx.docgen.Source;

/**
 * Simple example to write and read Pojos
 * 
 * @author Michael Remme
 * 
 */

@Source(translate = false)
public class Examples {
  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  /**
   * Init a MongoClient onto a locally running Mongo and the {@link dataStore}
   * 
   * @param vertx
   */
  public void example1(Vertx vertx) {
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "PojongoTestDatabase");
    // MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
    // dataStore dataStore = new dataStore(mongoClient);
  }

  /**
   * Create the object to be saved into the datastore
   */
  public void example2() {
    MiniMapper miniMapper = new MiniMapper();
    miniMapper.name = "my mini mapper";
    miniMapper.number = 20;
  }

  /**
   * Saving an instance intp the Datastore
   * 
   * @param dataStore
   * @param dm
   */
  public void example3(IDataStore dataStore, MiniMapper miniMapper) {
    IWrite<MiniMapper> write = dataStore.createWrite(MiniMapper.class);
    write.add(miniMapper);
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
   * @param dataStore
   */
  public void example4(IDataStore dataStore) {
    IQuery<MiniMapper> query = dataStore.createQuery(MiniMapper.class);
    query.setRootQueryPart(new FieldCondition("name", "my mini mapper"));
    query.execute(rResult -> {
      if (rResult.failed()) {
        logger.error(rResult.cause());
      } else {
        IQueryResult<MiniMapper> qr = rResult.result();
        qr.iterator().next(itResult -> {
          if (itResult.failed()) {
            logger.error(itResult.cause());
          } else {
            MiniMapper readMapper = itResult.result();
            logger.info("Query found id " + readMapper.id);
          }
        });
      }
    });
  }

  /**
   * Delete an instance from the Datastore
   * 
   * @param dataStore
   * @param mapper
   */
  public void example5(IDataStore dataStore, MiniMapper mapper) {
    IDelete<MiniMapper> delete = dataStore.createDelete(MiniMapper.class);
    delete.add(mapper);
    delete.delete(deleteResult -> {
      if (deleteResult.failed()) {
        logger.error("", deleteResult.cause());
      } else {
        logger.info(deleteResult.result().getOriginalCommand());
      }
    });
  }

  public void example6(IDataStore dataStore) {
    IQuery<MiniMapper> query = dataStore.createQuery(MiniMapper.class);
    query.setRootQueryPart(new FieldCondition("name", "test"));
    IDelete<MiniMapper> delete = dataStore.createDelete(MiniMapper.class);
    delete.setQuery(query);
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
  public void example7(Vertx vertx, Handler<AsyncResult<IDataStore>> handler) {
    try {
      DataStoreSettings settings = loadDataStoreSettings(vertx, "/some/path/to/settings.json");
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

  public DataStoreSettings loadDataStoreSettings(Vertx vertx, String path) {
    FileSystem fs = vertx.fileSystem();
    if (fs.existsBlocking(path)) {
      Buffer buffer = fs.readFileBlocking(path);
      DataStoreSettings settings = Json.decodeValue(buffer.toString(), DataStoreSettings.class);
      return settings;
    } else {
      DataStoreSettings settings = new DataStoreSettings(IDataStoreInit.class, "testdatabase");
      // fill the settings like you need, REPLACE IDataStoreInit with a real implementation
      fs.writeFileBlocking(path, Buffer.buffer(Json.encode(settings)));
      throw new FileSystemException("File did not exist and was created new in path " + path);
    }
  }

}
