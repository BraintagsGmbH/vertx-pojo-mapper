/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.exception.QueryParameterException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.util.Size;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IQueryExpression}
 * 
 * @author sschmitt
 * 
 */
public abstract class AbstractQueryExpression implements IQueryExpression {

  private int limit;
  private int offset;
  private IMapper<?> mapper;

  /**
   * Transforms the java value of the given field into a value that is suited for the database, via the configured type
   * handler(s).<br>
   * If the operator is a multi-value operator (e.g IN or NOT_IN), the value must be an instance of {@link Iterable}.
   * 
   * @param field
   *          the java field of the condition
   * @param operator
   *          the operator of the condition
   * @param value
   *          the value that will be transformed, must not be null
   * @param handler
   *          returns the transformed value
   */
  protected void transformValue(IField field, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    if (operator.isMultiValueOperator()) {
      if (!(value instanceof Iterable)) {
        handler.handle(
            Future.failedFuture(new QueryParameterException("Multivalued argument but not an instance of Iterable")));
        return;
      }
      handleMultipleValues(field, (Iterable<?>) value, handler);
    } else {
      handleSingleValue(field, value, handler);
    }
  }

  /**
   * Handle the transformation of a single value
   * 
   * @param field
   *          the java field of the condition
   * @param value
   *          the value that will be transformed
   * @param handler
   *          returns the transformed value
   */
  private void handleSingleValue(IField field, Object value, Handler<AsyncResult<Object>> handler) {
    field.getTypeHandler().intoStore(value, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(result.result().getResult()));
      }
    });
  }

  /**
   * Handle the transformation of multiple values
   * 
   * @param fieldName
   *          the java field of the condition
   * @param value
   *          the values of the condition, must not be empty
   * @param handler
   *          returns a {@link List} with the transformed values
   */
  private void handleMultipleValues(IField field, Iterable<?> value, Handler<AsyncResult<Object>> handler) {
    int count = Size.size(value);
    if (count == 0) {
      String message = String.format("Multivalued argument, but no values defined for search in field %s.%s",
          getMapper().getMapperClass().getName(), field);
      handler.handle(Future.failedFuture(new QueryParameterException(message)));
      return;
    }

    Iterator<?> it = value.iterator();
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    while (it.hasNext()) {
      Future<Object> future = Future.future();
      futures.add(future);
      Object singleValue = it.next();
      handleSingleValue(field, singleValue, future.completer());
    }

    CompositeFuture.all(futures).setHandler(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<Object> results = result.result().list();
        handler.handle(Future.succeededFuture(results));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setLimit(int, int)
   */
  @Override
  public void setLimit(int limit, int offset) {
    this.limit = limit;
    this.offset = offset;
  }

  /**
   * @return the limit for this query
   */
  protected int getLimit() {
    return limit;
  }

  /**
   * @return the offset (starting position) for this query
   */
  protected int getOffset() {
    return offset;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setMapper(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#getMapper()
   */
  @Override
  public IMapper<?> getMapper() {
    return mapper;
  }
}
