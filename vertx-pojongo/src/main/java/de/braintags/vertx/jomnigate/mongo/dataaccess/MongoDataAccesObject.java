package de.braintags.vertx.jomnigate.mongo.dataaccess;

import java.util.function.Function;

import com.mongodb.MongoCommandException;

import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import io.vertx.core.Future;
import io.vertx.ext.mongo.MongoClient;

interface MongoDataAccesObject<T> extends IDataAccessObject<T> {

  int RETRY_TIMEOUT = 500;
  int RATE_LIMIT_ERROR_CODE = 16500;
  int MAX_RETRIES = 9;
  int START_TRY_COUNT = 0;

  default String getCollection() {
    return getMapper().getTableInfo().getName();
  }

  default MongoClient getMongoClient() {
    return (MongoClient) ((MongoDataStore) getDataStore()).getClient();
  }

  default <A> Function<Throwable, Future<A>> retryMethod(final int tryCount,
      final Function<Integer, Future<A>> action) {
    return e -> {
      if (e instanceof MongoCommandException && ((MongoCommandException) e).getCode() == RATE_LIMIT_ERROR_CODE
          && tryCount < MAX_RETRIES) {
        Future<Void> f = Future.future();
        getDataStore().getVertx().setTimer(RETRY_TIMEOUT, res -> f.complete());
        return f.compose(v -> action.apply(tryCount + 1));
      } else {
        return Future.failedFuture(e);
      }
    };
  }
}
