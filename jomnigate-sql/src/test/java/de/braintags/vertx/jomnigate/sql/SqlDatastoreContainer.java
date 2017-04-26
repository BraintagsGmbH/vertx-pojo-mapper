package de.braintags.vertx.jomnigate.sql;

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.IDataStoreInit;
import de.braintags.vertx.jomnigate.testdatastore.AbstractDataStoreContainer;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import de.braintags.vertx.util.exception.InitException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * a datastore container for sql based datastores
 * 
 * @author Michael Remme
 * 
 */
public abstract class SqlDatastoreContainer extends AbstractDataStoreContainer {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlDatastoreContainer.class);

  @Override
  public final void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    LOGGER.info("Startup of " + getClass().getSimpleName());
    try {
      if (getDataStore() == null) {
        DataStoreSettings settings = createSettings();
        IDataStoreInit dsInit = settings.getDatastoreInit().newInstance();
        dsInit.initDataStore(vertx, settings, initResult -> {
          if (initResult.failed()) {
            LOGGER.error("could not start sql datastore", initResult.cause());
            handler.handle(Future.failedFuture(new InitException(initResult.cause())));
          } else {
            setDatastore(initResult.result());
            handler.handle(Future.succeededFuture());
          }
        });
      } else {
        handler.handle(Future.succeededFuture());
      }
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public final void shutdown(Handler<AsyncResult<Void>> handler) {
    LOGGER.info("shutdown performed");
    ((SqlDataStore) getDataStore()).shutdown(result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
      }
      setDatastore(null);
      handler.handle(Future.succeededFuture());
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.IDatastoreContainer#dropTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public final void dropTable(String tablename, Handler<AsyncResult<Void>> handler) {
    String command = "DROP TABLE IF EXISTS " + tablename;
    Future<Void> f = SqlUtil.executeCommand((SqlDataStore) getDataStore(), command);
    f.setHandler(handler);
  }

  @Override
  public final void clearTable(String tableName, Handler<AsyncResult<Void>> handler) {
    String command = "DELETE from " + tableName;
    Future<Void> f = SqlUtil.executeCommand((SqlDataStore) getDataStore(), command);
    f.setHandler(handler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.IDatastoreContainer#getExpectedTypehandlerName(java.lang.Class,
   * java.lang.String)
   */
  @Override
  public final String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass,
      String defaultName) {
    throw new UnsupportedOperationException("typehandlers are out");
  }

}
