package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlGeoPointTypeHandler extends AbstractTypeHandler {
  private static final String CREATE_STRING = "ST_GeomFromText('POINT( %s %s)')";

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public SqlGeoPointTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, GeoPoint.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source != null && ((GeoPoint) source).getCoordinates() != null
        && ((GeoPoint) source).getCoordinates().getValues().size() == 2) {
      String content = String.format(CREATE_STRING, ((GeoPoint) source).getCoordinates().getValues().get(0),
          ((GeoPoint) source).getCoordinates().getValues().get(1));
      success(new SqlFunction("ST_GeomFromText", content), resultHandler);
    } else {
      success(null, resultHandler);
    }
  }

  public static class SqlFunction {
    private String functionName;
    private String content;

    public SqlFunction(String name, String content) {
      this.functionName = name;
      this.content = content;
    }

    @Override
    public String toString() {
      throw new UnsupportedOperationException();
    }
  }
}
