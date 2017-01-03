/*
 * #%L vertx-pojongo %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.exception.QueryParameterException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.typehandler.IFieldParameterResult;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.Size;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * The abstract implementation of an {@link IQueryRambler} is defining the way, an {@link IQuery} is rambled and fills a
 * datastore specific object which later on is executed on the datastore
 * 
 * @author Michael Remme
 */
public abstract class AbstractQueryRambler implements IQueryRambler {
  private IQueryExpression queryExpression;
  private IMapper<?> mapper;

  /**
   * @param queryExpression
   *          the {@link IQueryExpression} to be used
   */
  public AbstractQueryRambler(IQueryExpression queryExpression) {
    this.queryExpression = queryExpression;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.IQuery)
   */
  @Override
  public final void start(IQuery<?> query) {
    if (mapper != null)
      throw new UnsupportedOperationException("sub query not implemented yet");
    mapper = query.getMapper();
    queryExpression.setMapper(mapper);
    if (query.getNativeCommand() != null) {
      queryExpression.setNativeCommand(query.getNativeCommand());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.IQuery)
   */
  @Override
  public void stop(IQuery<?> query) {
    // nothing to do here
  }

  private void handleCondition(IQueryCondition condition, Handler<AsyncResult<Void>> resultHandler) {
    switch (condition.getOperator()) {
    case IN:
    case NOT_IN:
      handleMultipleValues(condition, resultHandler);
      break;

    default:
      handleSingleValue(condition, resultHandler);
    }
  }

  /**
   * Create the argument for query parts, which define multiple arguments
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private final void handleMultipleValues(IQueryCondition fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = mapper.getField(fieldParameter.getField());
    IColumnInfo ci = field.getColumnInfo();
    if (ci == null) {
      resultHandler
          .handle(Future.failedFuture(new MappingException("Can't find columninfo for field " + field.getFullName())));
      return;
    }

    String operator = field.getMapper().getMapperFactory().getDataStore().getQueryOperatorTranslator()
        .translate(fieldParameter.getOperator());
    Object valueIterable = fieldParameter.getValue();
    if (!(valueIterable instanceof Iterable)) {
      resultHandler.handle(
          Future.failedFuture(new QueryParameterException("multivalued argument but not an instance of Iterable")));
      return;
    }
    int count = Size.size((Iterable<?>) valueIterable);
    if (count == 0) {
      String message = String.format(
          "multivalued argument but no values defined for search in field %s.%s with operator '%s'",
          field.getMapper().getMapperClass().getName(), field.getName(), fieldParameter.getOperator());
      resultHandler.handle(Future.failedFuture(new QueryParameterException(message)));
      return;
    }
    iterateMultipleValues(field, ci, operator, count, (Iterable<?>) valueIterable, resultHandler);
  }

  private final void iterateMultipleValues(IField field, IColumnInfo ci, String operator, int count,
      Iterable<?> valueIterable, Handler<AsyncResult<Void>> resultHandler) {
    CounterObject<Void> co = new CounterObject<>(count, resultHandler);
    Iterator<?> values = valueIterable.iterator();
    JsonArray resultArray = new JsonArray();

    while (values.hasNext() && !co.isError()) {
      Object value = values.next();
      field.getTypeHandler().intoStore(value, field, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        } else {
          resultArray.add(result.result().getResult());

          if (co.reduce()) {
            String colName = ci.getName();
            queryExpression.addQuery(colName, operator, resultArray);
            resultHandler.handle(Future.succeededFuture());
          }
        }
      });
    }
  }

  /**
   * Create the argument for query parts, which define one single argument
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private final void handleSingleValue(IQueryCondition fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = mapper.getField(fieldParameter.getField());
    field.getTypeHandler().handleFieldParameter(field, fieldParameter, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        add(result.result());
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

  protected void add(IFieldParameterResult fr) {
    queryExpression.addQuery(fr);
  }

  /**
   * THe mapper used by the current instance
   * 
   * @return the mapper
   */
  public final IMapper<?> getMapper() {
    return mapper;
  }

  /**
   * THe mapper used by the current instance
   * 
   * @param mapper
   *          the mapper to set
   */
  public final void setMapper(IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /**
   * Get the sql statement
   * 
   * @return the generated {@link IQueryExpression}
   */
  public IQueryExpression getQueryExpression() {
    return queryExpression;
  }

  @Override
  public String toString() {
    return queryExpression.toString();
  }

  @Override
  public void apply(IQueryCondition queryCondition, Handler<AsyncResult<Void>> resultHandler) {
    handleCondition(queryCondition, resultHandler);
  }

  @Override
  public void apply(IQueryContainer queryContainer, Handler<AsyncResult<Void>> resultHandler) {
    queryExpression.startConnectorBlock();
    List<IQueryPart> content = queryContainer.getContent();
    CounterObject<Void> co = new CounterObject<>(content.size(), resultHandler);
    Iterator<IQueryPart> it = content.iterator();
    while (it.hasNext() && !co.isError()) {
      IQueryPart queryPart = it.next();
      queryPart.applyTo(this, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        } else {
          if (it.hasNext())
            queryExpression.connect(queryContainer.getConnector());

          if (co.reduce()) {
            queryExpression.stopConnectorBlock();
            resultHandler.handle(Future.succeededFuture());
          }
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition, io.vertx.core.Handler)
   */
  @Override
  public void apply(ISortDefinition<?> sortDefinition, Handler<AsyncResult<Void>> resultHandler) {
    try {
      queryExpression.addSort(sortDefinition);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }

  }
}
