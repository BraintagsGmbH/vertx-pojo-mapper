package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * <br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public class FieldCondition implements IQueryCondition {

  private String field;
  private QueryOperator operator;
  private Object value;

  public FieldCondition(String field, Object value) {
    this(field, QueryOperator.EQUALS, value);
  }

  public FieldCondition(String field, QueryOperator logic, Object value) {
    this.field = field;
    this.operator = logic;
    this.value = value;
  }

  public static FieldCondition near(String field, double x, double y, int maxDistance) {
    return new FieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(x, y, new double[0])), maxDistance));
  }

  /**
   * @return the field
   */
  @Override
  public String getField() {
    return field;
  }

  /**
   * @return the logic
   */
  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  /**
   * @return the value
   */
  @Override
  public Object getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IRamblerSource#applyTo(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQueryRambler, io.vertx.core.Handler)
   */
  @Override
  public void applyTo(IQueryRambler ramblerHandler, Handler<AsyncResult<Void>> resultHandler) {
    ramblerHandler.apply(this, result -> {
      if (result.failed())
        resultHandler.handle(Future.failedFuture(result.cause()));
      else
        resultHandler.handle(Future.succeededFuture());
    });
  }
}
