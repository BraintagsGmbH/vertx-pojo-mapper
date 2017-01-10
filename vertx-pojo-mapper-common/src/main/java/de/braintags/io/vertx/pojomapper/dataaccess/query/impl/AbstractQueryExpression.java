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
 * 
 * 
 * @author sschmitt
 * 
 */
public abstract class AbstractQueryExpression implements IQueryExpression {

  private int limit;
  private int offset;
  private IMapper<?> mapper;

  protected void transformValue(IField field, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    if (operator == QueryOperator.IN || operator == QueryOperator.NOT_IN) {
      handleMultipleValues(field, operator, value, handler);
    } else {
      handleSingleValue(field, value, handler);
    }
  }

  /**
   * @param fieldName
   * @param value
   * @param handler
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
   * @param fieldName
   * @param operator
   * @param value
   * @param handler
   */
  private void handleMultipleValues(IField field, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    if (!(value instanceof Iterable)) {
      handler.handle(
          Future.failedFuture(new QueryParameterException("Multivalued argument but not an instance of Iterable")));
      return;
    }

    int count = Size.size((Iterable<?>) value);
    if (count == 0) {
      String message = String.format(
          "multivalued argument but no values defined for search in field %s.%s with operator '%s'",
          getMapper().getMapperClass().getName(), field, operator);
      handler.handle(Future.failedFuture(new QueryParameterException(message)));
      return;
    }

    Iterator<?> it = ((Iterable<?>) value).iterator();
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

  @Override
  public void setLimit(int limit, int offset) {
    this.limit = limit;
    this.offset = offset;
  }

  /**
   * @return the limit
   */
  protected int getLimit() {
    return limit;
  }

  /**
   * @return the offset
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
