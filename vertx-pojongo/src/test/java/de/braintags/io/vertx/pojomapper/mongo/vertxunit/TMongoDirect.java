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
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.groovy.core.buffer.Buffer;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoDirect extends DatastoreBaseTest {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDirect.class);

  @Test
  public void simpleTest(TestContext context) {
    LOGGER.info("-->>test");
    MongoDataStore ds = (MongoDataStore) getDataStore();
    MongoClient client = ds.getMongoClient();

    JsonObject jsonCommand = new JsonObject();
    // getNextSequenceValue("productid")
    jsonCommand.put("_id", "getNextSequenceValue(\"productid\")".getBytes());
    jsonCommand.put("name", "testName");
    client.insert("nativeCommandCollection", jsonCommand, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
      } else {
        LOGGER.info("executed: " + result.result());
      }

    });

  }

  public String getFuntion() {
    Buffer buffer = Buffer.buffer();
    buffer.appendString("function getNextSequenceValue(sequenceName){").appendString("\n");
    buffer.appendString("  var sequenceDocument = db.counters.findAndModify({").appendString("\n");
    buffer.appendString("     query:{_id: sequenceName },").appendString("\n");
    buffer.appendString("     update: {$inc:{sequence_value:1}},").appendString("\n");
    buffer.appendString("     new:true").appendString("\n");
    buffer.appendString("  });").appendString("\n");
    buffer.appendString("  return sequenceDocument.sequence_value;").appendString("\n");
    buffer.appendString("}").appendString("\n");
    return buffer.toString();
  }

  /*
   * function getNextSequenceValue(sequenceName){
   * 
   * var sequenceDocument = db.counters.findAndModify({
   * query:{_id: sequenceName },
   * update: {$inc:{sequence_value:1}},
   * new:true
   * });
   * 
   * return sequenceDocument.sequence_value;
   * }
   * 
   */
}
