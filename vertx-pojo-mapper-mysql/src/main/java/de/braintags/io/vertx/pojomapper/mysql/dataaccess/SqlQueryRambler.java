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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.exception.QueryParameterException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.Size;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * FIrst creates a query tree as JsonObject and then from the JsonObject the statement
 * 
 * @author Michael Remme
 * 
 */

public class SqlQueryRambler implements IQueryRambler {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlQueryRambler.class);
  private static final String SELECT_STATEMENT = "SELECT * from %s";
  private IMapper mapper;
  private SqlExpression statement = new SqlExpression();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQuery)
   */
  @Override
  public void start(IQuery<?> query) {
    if (mapper != null)
      throw new UnsupportedOperationException("sub query not implemented yet");
    mapper = query.getMapper();
    statement.addSelect(String.format(SELECT_STATEMENT, mapper.getTableInfo().getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQuery)
   */
  @Override
  public void stop(IQuery<?> query) {
    // nothing to do here
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ILogicContainer)
   */
  @Override
  public void start(ILogicContainer<?> container) {
    String logic = QueryLogicTranslator.translate(container.getLogic());
    statement.startConnectorBlock(logic);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ILogicContainer)
   */
  @Override
  public void stop(ILogicContainer<?> container) {
    statement.stopConnectorBlock();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IFieldParameter, io.vertx.core.Handler)
   */
  @Override
  public void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {

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
   * Create the argument for query parts, which define one single argument
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private void handleMultipleValues(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      resultHandler
          .handle(Future.failedFuture(new MappingException("Can't find columninfo for field " + field.getFullName())));
      return;
    }

    String operator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object valueIterable = fieldParameter.getValue();
    if (!(valueIterable instanceof Iterable)) {
      resultHandler.handle(
          Future.failedFuture(new QueryParameterException("multivalued argument but not an instance of Iterable")));
      return;
    }
    int count = Size.size((Iterable<?>) valueIterable);
    if (count == 0) {
      resultHandler
          .handle(Future.failedFuture(new QueryParameterException("multivalued argument but no values defined")));
      return;
    }
    iterateMultipleValues(field, ci, operator, count, (Iterable<?>) valueIterable, resultHandler);
  }

  private void iterateMultipleValues(IField field, IColumnInfo ci, String operator, int count,
      Iterable<?> valueIterable, Handler<AsyncResult<Void>> resultHandler) {
    CounterObject co = new CounterObject(count);
    Iterator<?> values = valueIterable.iterator();
    ErrorObject<Void> errorObject = new ErrorObject<Void>(resultHandler);
    JsonArray resultArray = new JsonArray();

    while (values.hasNext() && !errorObject.isError()) {
      Object value = values.next();
      field.getTypeHandler().intoStore(value, field, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
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
  private void handleSingleValue(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      resultHandler
          .handle(Future.failedFuture(new MappingException("Can't find columninfo for field " + field.getFullName())));
      return;
    }
    String operator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object value = fieldParameter.getValue();

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IFieldParameter)
   */
  @Override
  public void stop(IFieldParameter<?> fieldParameter) {
    // nothing to do here
  }

  private void add(String colName, String operator, Object objectToAdd) {
    statement.addQuery(colName, operator, objectToAdd);
  }

  /**
   * Get the sql statement
   * 
   * @return the generated {@link SqlExpression}
   */
  public SqlExpression getSqlStatement() {
    return statement;
  }

}
