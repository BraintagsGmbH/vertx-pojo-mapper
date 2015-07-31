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

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class Examples {
  private static final Logger logger = LoggerFactory.getLogger(Examples.class);

  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  /**
   * 
   */
  public Examples() {
  }

  public static void stopMongo() {
    logger.info("STOPPING MONGO");
    if (mongoClient != null)
      mongoClient.close();
  }

  /**
   * Get the connection String for the mongo db
   * 
   * @return
   */
  protected static String getConnectionString() {
    return getProperty("connection_string");
  }

  /**
   * Get the name of the database to be used
   * 
   * @return
   */
  protected static String getDatabaseName() {
    return getProperty("db_name");
  }

  /**
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  protected static String getProperty(String name) {
    String s = System.getProperty(name);
    return s == null ? null : s.trim();
  }

  public static void main(String[] args) {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");

  }
}
