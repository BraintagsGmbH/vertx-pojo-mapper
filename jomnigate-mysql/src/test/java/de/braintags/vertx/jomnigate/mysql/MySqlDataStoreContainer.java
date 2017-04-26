package de.braintags.vertx.jomnigate.mysql;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.mysql.init.MySqlDataStoreInit;
import de.braintags.vertx.jomnigate.sql.SqlDatastoreContainer;

/**
 * 
 * @author Michael Remme
 * 
 */
public class MySqlDataStoreContainer extends SqlDatastoreContainer {

  @Override
  public DataStoreSettings createSettings() {
    return MySqlDataStoreInit.createSettings();
  }

}
