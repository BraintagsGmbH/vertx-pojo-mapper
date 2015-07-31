/*
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package examples;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.test.core.VertxTestBase;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * Simple example to write and read Pojos
 * 
 * @author Michael Remme
 * 
 */

public class Examples extends VertxTestBase {
  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  @Test
  public void Demo() {
    try {
      /*
       * Init a MongoClient onto a locally running Mongo
       */
      JsonObject config = new JsonObject();
      config.put("connection_string", "mongodb://localhost:27017");
      config.put("db_name", "PojongoTestDatabase");
      mongoClient = MongoClient.createNonShared(vertx, config);
      mongoDataStore = new MongoDataStore(mongoClient);

      DemoMapper dm = new DemoMapper();
      dm.setName("demoMapper");
      DemoSubMapper dmsr = new DemoSubMapper();
      dmsr.subname = "referenced submapper";
      dm.subMapperReferenced = dmsr;

      DemoSubMapper dmse = new DemoSubMapper();
      dmse.subname = "referenced submapper";
      dm.subMapperEmbedded = dmse;

      IWrite<DemoMapper> write = mongoDataStore.createWrite(DemoMapper.class);
      write.add(dm);
      write.save(result -> {
        if (result.failed()) {
          logger.error(result.cause());
          fail(result.cause().getMessage());
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
            } else {
              IQueryResult<DemoMapper> qr = rResult.result();
              qr.iterator().next(itResult -> {
                if (itResult.failed()) {
                  logger.error(itResult.cause());
                  fail(itResult.cause().getMessage());
                } else {
                  DemoMapper readMapper = itResult.result();
                  logger.info("id " + readMapper.id);

                }
              });
            }
          });

        }
      });
    } finally {
      if (mongoClient != null)
        mongoClient.close();
    }

  }

  public class DemoMapper {
    @Id
    public String id;
    private String name;
    @Embedded
    public DemoSubMapper subMapperEmbedded;
    @Referenced
    public DemoSubMapper subMapperReferenced;

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * @param name
     *          the name to set
     */
    public void setName(String name) {
      this.name = name;
    }

  }

  public class DemoSubMapper {
    @Id
    public String id;
    public String subname;

  }
}
