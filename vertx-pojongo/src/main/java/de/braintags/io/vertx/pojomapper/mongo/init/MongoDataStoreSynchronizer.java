package de.braintags.io.vertx.pojomapper.mongo.init;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoDataStoreSynchronizer extends AbstractDataStoreSynchronizer<JsonObject> {
  private MongoDataStore ds;

  public MongoDataStoreSynchronizer(MongoDataStore ds) {
    this.ds = ds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer#synchronize(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  public void synchronize(IMapper mapper, Handler<AsyncResult<ISyncResult<JsonObject>>> resultHandler) {
    throw new UnsupportedOperationException();
  }

}
