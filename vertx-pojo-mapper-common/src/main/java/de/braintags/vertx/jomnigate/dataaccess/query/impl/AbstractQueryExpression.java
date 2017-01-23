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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.IFieldValueResolver;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer;
import de.braintags.vertx.jomnigate.dataaccess.query.IVariableFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownSearchConditionException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.VariableSyntaxException;
import de.braintags.vertx.jomnigate.exception.QueryParameterException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.util.Size;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IQueryExpression}
 *
 * @param T
 *          the internal result type of the search condition building process
 *
 * @author sschmitt
 *
 */
public abstract class AbstractQueryExpression<T> implements IQueryExpression {

  private int limit;
  private int offset;
  private IMapper<?> mapper;

  /**
   * Transforms the java value of the given field into a value that is suited for the database, via the configured type
   * handler(s).<br>
   * If the operator is a multi-value operator (e.g IN or NOT_IN), the value must be an instance of {@link Iterable}.
   * The result will then also be a List of the transformed values.
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
  private void transformValue(IField field, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    if (operator.isMultiValueOperator()) {
      if (value instanceof Iterable) {
        handleMultipleValues(field, (Iterable<?>) value, handler);
      } else {
        handler.handle(Future.failedFuture(
            new QueryParameterException("Multivalued argument but not an instance of Iterable:" + value)));
      }
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
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#buildQueryExpression(de.braintags.vertx.
   * pojomapper.dataaccess.query.ISearchCondition, io.vertx.core.Handler)
   */
  @Override
  public void buildSearchCondition(ISearchCondition searchCondition, IFieldValueResolver resolver,
      Handler<AsyncResult<Void>> handler) {
    internalBuildSearchCondition(searchCondition, resolver, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handleFinishedSearchCondition(result.result());
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Method to process the final result of the search condition building process, mostly to save it to a field and/or do
   * some final manipulation
   *
   * @param result
   *          the final result of the search condition build process
   */
  protected abstract void handleFinishedSearchCondition(T result);

  /**
   * Build the abstract search condition into the native search condition of the query. Can be used recursively for
   * conditions that contain more than one sub condition (AND, OR, ..)
   * If the condition is an {@link IFieldCondition}, and was already built before and cached, this method
   * directly returns the result without rebuilding it
   *
   * @param searchCondition
   *          the query search condition
   * @param handler
   *          returns the internal object that represents the given search condition
   */
  @SuppressWarnings("unchecked")
  protected void internalBuildSearchCondition(ISearchCondition searchCondition, IFieldValueResolver resolver,
      Handler<AsyncResult<T>> handler) {
    if (searchCondition instanceof IFieldCondition) {
      IFieldCondition fieldCondition = (IFieldCondition) searchCondition;
      Object cachedResult = fieldCondition.getIntermediateResult(getClass());
      if (cachedResult != null) {
        /*
         * the returned result is specific to the query expression class, so only the correct
         * result type should be returned
         */
        handler.handle(Future.succeededFuture((T) cachedResult));
      } else {
        parseFieldCondition(fieldCondition, resolver, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            // cache the result in the search condition before forwarding it to the handler
            fieldCondition.setIntermediateResult(getClass(), result.result());
            handler.handle(Future.succeededFuture(result.result()));
          }
        });
      }
    } else if (searchCondition instanceof ISearchConditionContainer) {
      parseSearchConditionContainer((ISearchConditionContainer) searchCondition, resolver, handler);
    } else {
      handler.handle(Future.failedFuture(new UnknownSearchConditionException(searchCondition)));
    }
  }

  /**
   * Parses a {@link IFieldCondition}. The operator and value will be transformed into a format fitting the concrete
   * database. The java field name will be converted to the matching column name of the database. If its an
   * {@link IVariableFieldCondition}, the variable will be resolved with the given resolver
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param handler
   *          returns the internal object that represents the given field condition
   */
  protected void parseFieldCondition(IFieldCondition fieldCondition, IFieldValueResolver resolver,
      Handler<AsyncResult<T>> handler) {
    IField field = getMapper().getField(fieldCondition.getField());
    String columnName = field.getColumnInfo().getName();
    Object fieldValue = fieldCondition.getValue();
    if (fieldValue != null) {
      if (fieldCondition instanceof IVariableFieldCondition) {
        try {
          fieldValue = resolver.resolve((String) fieldValue);
        } catch (VariableSyntaxException e) {
          handler.handle(Future.failedFuture(e));
          return;
        }
      }
      transformValue(field, fieldCondition.getOperator(), fieldValue, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          try {
            T fieldConditionResult = buildFieldConditionResult(fieldCondition, columnName, result.result());
            handler.handle(Future.succeededFuture(fieldConditionResult));
          } catch (UnknownQueryOperatorException e) {
            handler.handle(Future.failedFuture(e));
          }
        }
      });
    } else {
      handleNullConditionValue(fieldCondition, columnName, handler);
    }
  }

  /**
   * Build the result of a field condition with the parsed value
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param columnName
   *          the column name of the database
   * @param parsedValue
   *          the value after being transformed by the matching type handler
   * @return
   */
  protected abstract T buildFieldConditionResult(IFieldCondition fieldCondition, String columnName, Object parsedValue)
      throws UnknownQueryOperatorException;

  /**
   * Special case for null values, to avoid costly, unneeded transformation
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param columnName
   *          the column name of the database
   * @param handler
   *          returns the result of the parse process, or an error if e.g. the operator or the database can not handle
   *          null values
   */
  protected abstract void handleNullConditionValue(IFieldCondition fieldCondition, final String columnName,
      Handler<AsyncResult<T>> handler);

  /**
   * Parses the container part of a search condition. Loops through the content of the container and connects each
   * resulting condition with the specified connector.
   *
   * @param container
   *          the container to parse
   * @param handler
   *          returns the result of the condition parsing
   */
  protected void parseSearchConditionContainer(ISearchConditionContainer container, IFieldValueResolver resolver,
      Handler<AsyncResult<T>> handler) {
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (ISearchCondition searchCondition : container.getConditions()) {
      Future<T> future = Future.future();
      futures.add(future);
      internalBuildSearchCondition(searchCondition, resolver, future.completer());
    }

    CompositeFuture.all(futures).setHandler(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<T> parsedConditionList = result.result().list();
        T parsedContainer;
        try {
          parsedContainer = parseContainerContents(parsedConditionList, container);
          handler.handle(Future.succeededFuture(parsedContainer));
        } catch (UnknownQueryLogicException e) {
          handler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /**
   * Builds the parse result containing all parsed conditions of a container
   *
   * @param parsedConditionList
   *          the parsed search conditions of the container
   * @param container
   *          the container to parse
   * @return
   */
  protected abstract T parseContainerContents(List<T> parsedConditionList, ISearchConditionContainer container)
      throws UnknownQueryLogicException;

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#setLimit(int, int)
   */
  @Override
  public void setLimit(int limit, int offset) {
    this.limit = limit;
    this.offset = offset;
  }

  /**
   * @return the limit for this query
   */
  @Override
  public int getLimit() {
    return limit;
  }

  /**
   * @return the offset (starting position) for this query
   */
  @Override
  public int getOffset() {
    return offset;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#setMapper(de.braintags.vertx.jomnigate.
   * mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#getMapper()
   */
  @Override
  public IMapper<?> getMapper() {
    return mapper;
  }
}
