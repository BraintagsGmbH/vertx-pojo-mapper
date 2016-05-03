/*
 * #%L
 * vertx-pojongo
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

import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.exception.QueryParameterException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
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
 * 
 */
public abstract class AbstractQueryRambler implements IQueryRambler {
  private IQueryExpression queryExpression;
  private IQueryLogicTranslator logicTranslator;
  private IQueryOperatorTranslator queryOperatorTranslator;
  private IMapper mapper;

  /**
   * 
   * @param queryExpression
   *          the {@link IQueryExpression} to be used
   * @param logicTranslator
   *          responsible to translate logic idioms for the current implementation
   * @param queryOperatorTranslator
   *          responsible to translate query operators for the current implementation
   */
  public AbstractQueryRambler(IQueryExpression queryExpression, IQueryLogicTranslator logicTranslator,
      IQueryOperatorTranslator queryOperatorTranslator) {
    this.queryExpression = queryExpression;
    this.logicTranslator = logicTranslator;
    this.queryOperatorTranslator = queryOperatorTranslator;
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ILogicContainer)
   */
  @Override
  public final void start(ILogicContainer<?> container) {
    String logic = logicTranslator.translate(container.getLogic());
    queryExpression.startConnectorBlock(logic, logicTranslator.opensParenthesis(container.getLogic()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ILogicContainer)
   */
  @Override
  public final void stop(ILogicContainer<?> container) {
    queryExpression.stopConnectorBlock();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition)
   */
  @Override
  public void start(ISortDefinition<?> sortDefinition) {
    queryExpression.addSort(sortDefinition);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition)
   */
  @Override
  public void stop(ISortDefinition<?> sortDefinition) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.IFieldParameter, io.vertx.core.Handler)
   */
  @Override
  public final void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    switch (fieldParameter.getOperator()) {
    case IN:
    case NOT_IN:
      handleMultipleValues(fieldParameter, resultHandler);
      break;

    default:
      handleSingleValue(fieldParameter, resultHandler);
    }

  }

  /**
   * Create the argument for query parts, which define multiple arguments
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private final void handleMultipleValues(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      resultHandler
          .handle(Future.failedFuture(new MappingException("Can't find columninfo for field " + field.getFullName())));
      return;
    }

    String operator = queryOperatorTranslator.translate(fieldParameter.getOperator());
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
            add(colName, operator, resultArray);
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
  private final void handleSingleValue(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      resultHandler
          .handle(Future.failedFuture(new MappingException("Can't find columninfo for field " + field.getFullName())));
      return;
    }
    String operator = queryOperatorTranslator.translate(fieldParameter.getOperator());
    Object value = translateValue(fieldParameter.getOperator(), fieldParameter.getValue());

    field.getTypeHandler().intoStore(value, field, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        Object storeObject = result.result().getResult();
        add(ci.getName(), operator, storeObject);
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

  protected Object translateValue(QueryOperator operator, Object value) {
    return value;
  }

  private final void add(String colName, String operator, Object objectToAdd) {
    queryExpression.addQuery(colName, operator, objectToAdd);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.IFieldParameter)
   */
  @Override
  public final void stop(IFieldParameter<?> fieldParameter) {
    if (fieldParameter.isCloseParenthesis())
      queryExpression.closeParenthesis();
  }

  /**
   * THe mapper used by the current instance
   * 
   * @return the mapper
   */
  public final IMapper getMapper() {
    return mapper;
  }

  /**
   * THe mapper used by the current instance
   * 
   * @param mapper
   *          the mapper to set
   */
  public final void setMapper(IMapper mapper) {
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

}
