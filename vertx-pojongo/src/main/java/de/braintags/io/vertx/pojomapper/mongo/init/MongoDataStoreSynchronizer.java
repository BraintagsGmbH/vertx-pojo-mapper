package de.braintags.io.vertx.pojomapper.mongo.init;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Index;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer#internalSyncronize(de.braintags.io.
   * vertx.pojomapper.mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  protected void internalSyncronize(IMapper mapper, Handler<AsyncResult<ISyncResult<JsonObject>>> resultHandler) {
    if (mapper.getIndexDefinitions() == null) {
      resultHandler.handle(Future.succeededFuture());
    } else {
      List<Future> indexFutures = new ArrayList<>();
      for (Index index : mapper.getIndexDefinitions().value()) {
        Future f = Future.future();
        indexFutures.add(f);
        handleIndex(index, f);
      }

      CompositeFuture.all(indexFutures).setHandler(cfRes -> {
        if (cfRes.failed()) {
          resultHandler.handle(Future.failedFuture(cfRes.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(getSyncResult()));
        }
      });

    }
  }

  private void handleIndex(Index index, Future f) {
    f.fail(new UnsupportedOperationException());
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

}
