/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.Arrays;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IRamblerSource;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * IMplementation of {@link IFieldParameter}
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the parent container as extension of {@link IQueryContainer}
 */

public class FieldParameter<T extends IQueryContainer> implements IFieldParameter<T>, IRamblerSource {
  private IField field;
  private Object value;
  private QueryOperator operator;
  private T container;
  private boolean closeParenthesis = false;

  /**
   * Create an instance with the given container for the given field
   * 
   * @param container
   *          the parent container
   * @param field
   *          the underlaying field
   */
  public FieldParameter(T container, IField field) {
    this.container = container;
    this.field = field;
    if (field == null)
      throw new NullPointerException("field is undefined");
  }

  @Override
  public IField getField() {
    return field;
  }

  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public T is(Object value) {
    return addValue(QueryOperator.EQUALS, value);
  }

  @Override
  public T isNot(Object value) {
    return addValue(QueryOperator.NOT_EQUALS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#larger(java.lang.Object)
   */
  @Override
  public T larger(Object value) {
    return addValue(QueryOperator.LARGER, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#largerEqual(java.lang.Object)
   */
  @Override
  public T largerEqual(Object value) {
    return addValue(QueryOperator.LARGER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#less(java.lang.Object)
   */
  @Override
  public T less(Object value) {
    return addValue(QueryOperator.SMALLER, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#lessEqual(java.lang.Object)
   */
  @Override
  public T lessEqual(Object value) {
    return addValue(QueryOperator.SMALLER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#in(java.lang.Object)
   */
  @Override
  public T in(Iterable<?> value) {
    return addValue(QueryOperator.IN, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#notIn(java.lang.Object)
   */
  @Override
  public T notIn(Iterable<?> value) {
    return addValue(QueryOperator.NOT_IN, value);
  }

  private T addValue(QueryOperator op, Object value) {
    this.operator = op;
    this.value = value;
    return container;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#starts(java.lang.Object)
   */
  @Override
  public T starts(Object value) {
    return addValue(QueryOperator.STARTS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#ends(java.lang.Object)
   */
  @Override
  public T ends(Object value) {
    return addValue(QueryOperator.ENDS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#contains(java.lang.Object)
   */
  @Override
  public T contains(Object value) {
    return addValue(QueryOperator.CONTAINS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IRamblerSource#applyTo(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.impl.IQueryRambler)
   */
  @Override
  public void applyTo(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.start(this, result -> {
      if (result.failed()) {
        resultHandler.handle(result);
      } else {
        rambler.stop(this);
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#in(java.lang.Object[])
   */
  @Override
  public T in(Object... values) {
    return in(Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#notIn(java.lang.Object[])
   */
  @Override
  public T notIn(Object... values) {
    return notIn(Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#isCloseParenthesis()
   */
  @Override
  public boolean isCloseParenthesis() {
    return closeParenthesis;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#setCloseParenthesis(boolean)
   */
  @Override
  public void setCloseParenthesis(boolean doClose) {
    this.closeParenthesis = doClose;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#near(double, double, int)
   */
  @Override
  public T near(double x, double y, int maxDistance) {
    return near(new GeoPoint(new Position(x, y)), maxDistance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter#near(de.braintags.io.vertx.pojomapper.datatypes.
   * geojson.GeoPoint, int)
   */
  @Override
  public T near(GeoPoint point, int maxDistance) {
    return addValue(QueryOperator.NEAR, new GeoSearchArgument(point, maxDistance));
  }

}
