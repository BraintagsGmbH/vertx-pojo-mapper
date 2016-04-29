package de.braintags.io.vertx.pojomapper.mongo.init;

import de.braintags.io.vertx.pojomapper.annotation.Indexes;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.MongoUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * MongoDataStoreSynchronizer checks / creates needed indexes
 * 
 * @author Michael Remme
 * 
 */
public class MongoDataStoreSynchronizer extends AbstractDataStoreSynchronizer<JsonObject> {
  private MongoSyncResult syncResult = new MongoSyncResult();
  private MongoDataStore ds;

  public MongoDataStoreSynchronizer(MongoDataStore ds) {
    this.ds = ds;
  }

  @Override
  protected void syncTable(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    resultHandler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer#getSyncResult()
   */
  @Override
  protected ISyncResult<JsonObject> getSyncResult() {
    return syncResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer#syncIndexes(de.braintags.io.vertx.
   * pojomapper.mapping.IMapper, de.braintags.io.vertx.pojomapper.annotation.Indexes, io.vertx.core.Handler)
   */
  @Override
  protected void syncIndexes(IMapper mapper, Indexes indexes, Handler<AsyncResult<Void>> resultHandler) {
    MongoUtil.createIndexes(ds, mapper.getTableInfo().getName(), mapper.getIndexDefinitions(), result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

}
