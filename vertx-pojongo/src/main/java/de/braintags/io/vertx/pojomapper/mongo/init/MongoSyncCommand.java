package de.braintags.io.vertx.pojomapper.mongo.init;

import de.braintags.io.vertx.pojomapper.mapping.ISyncCommand;
import de.braintags.io.vertx.pojomapper.mapping.SyncAction;
import io.vertx.core.json.JsonObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoSyncCommand implements ISyncCommand<JsonObject> {
  private final SyncAction syncAction;
  private JsonObject syncCommand;

  public MongoSyncCommand(SyncAction action) {
    this.syncAction = action;
  }

  public MongoSyncCommand(SyncAction action, JsonObject command) {
    this.syncAction = action;
    this.syncCommand = command;
  }

  @Override
  public JsonObject getCommand() {
    return syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#getAction()
   */
  @Override
  public SyncAction getAction() {
    return syncAction;
  }

}
