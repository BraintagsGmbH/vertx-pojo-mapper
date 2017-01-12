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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.QueryExpressionBuildException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownSearchConditionException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition.SortArgument;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.mapping.SqlMapper;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlDistanceSearchFunction;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * A helper object, which contains all elements of an sql expression, like the select part, where clause and additional
 * parts
 * 
 * @author Michael Remme
 * 
 */

public class SqlExpression extends AbstractQueryExpression {
  private static final String SELECT_STATEMENT = "SELECT %s from %s";
  private static final String DELETE_STATEMENT = "DELETE from %s";
  private static final String COUNT_STATEMENT = "SELECT count(*) from %s";

  private String nativeCommand = null;
  private StringBuilder select = new StringBuilder();
  private StringBuilder delete = new StringBuilder();
  private StringBuilder count = new StringBuilder();

  private StringBuilder whereClause = new StringBuilder();
  private StringBuilder orderByClause = new StringBuilder();
  private JsonArray parameters = new JsonArray();

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#setMapper(de.braintags.io.vertx.
   * pojomapper.mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper<?> mapper) {
    super.setMapper(mapper);
    select.append(
        String.format(SELECT_STATEMENT, ((SqlMapper<?>) mapper).getQueryFieldNames(), mapper.getTableInfo().getName()));
    delete.append(String.format(DELETE_STATEMENT, mapper.getTableInfo().getName()));
    count.append(String.format(COUNT_STATEMENT, mapper.getTableInfo().getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object nativeCommand) {
    if (nativeCommand instanceof CharSequence) {
      this.nativeCommand = nativeCommand.toString();
    } else {
      throw new UnsupportedOperationException(
          "the sql datastore needs a CharSequence as native format, but is " + nativeCommand.getClass().getName());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#buildQueryExpression(de.braintags.io.vertx.
   * pojomapper.dataaccess.query.ISearchCondition, io.vertx.core.Handler)
   */
  @Override
  public void buildQueryExpression(ISearchCondition searchCondition, Handler<AsyncResult<Void>> handler) {
    internalBuildSearchCondition(searchCondition, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        SqlWhereFragment fragment = result.result();
        whereClause.append(fragment.whereClause);
        parameters.addAll(fragment.parameters);
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Build the abstract search condition into the native search condition of the query. Can be used recursively for
   * conditions that contain more than one sub condition (AND, OR, ..)
   * 
   * @param searchCondition
   *          the query search condition
   * @param handler
   *          returns a fragment that contains the SQL query clause of this condition, and possibly parameters that are
   *          used in this clause
   */
  private void internalBuildSearchCondition(ISearchCondition searchCondition,
      Handler<AsyncResult<SqlWhereFragment>> handler) {
    if (searchCondition instanceof IFieldCondition) {
      parseFieldCondition((IFieldCondition) searchCondition, handler);
    } else if (searchCondition instanceof ISearchConditionContainer) {
      parseSearchConditionContainer((ISearchConditionContainer) searchCondition, handler);
    } else {
      handler.handle(Future.failedFuture(new UnknownSearchConditionException(searchCondition)));
    }
  }

  /**
   * Parses a {@link IFieldCondition}. The operator and value will be transformed into a format fitting the SQL
   * database
   * 
   * @param fieldCondition
   * @param handler
   */
  private void parseFieldCondition(IFieldCondition fieldCondition, Handler<AsyncResult<SqlWhereFragment>> handler) {
    final IField field = getMapper().getField(fieldCondition.getField());
    final String columnName = field.getColumnInfo().getName();
    if (fieldCondition.getValue() != null) {
      transformValue(field, fieldCondition.getOperator(), fieldCondition.getValue(), result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          Object parsedValue = result.result();
          String parsedOperator;
          try {
            parsedOperator = translateOperator(fieldCondition.getOperator());
          } catch (UnknownQueryOperatorException e) {
            handler.handle(Future.failedFuture(e));
            return;
          }
          SqlWhereFragment fragment = buildConditionFragment(columnName, parsedOperator, parsedValue);
          handler.handle(Future.succeededFuture(fragment));
        }
      });
    } else {
      handleNullConditionValue(fieldCondition, columnName, handler);
    }
  }

  /**
   * Special case for null values, to avoid costly, unneeded transformation
   * 
   * @param fieldCondition
   * @param columnName
   * @param handler
   */
  private void handleNullConditionValue(IFieldCondition fieldCondition, final String columnName,
      Handler<AsyncResult<SqlWhereFragment>> handler) {
    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append(columnName).append(" ");
    if (fieldCondition.getOperator() == QueryOperator.EQUALS) {
      fragment.whereClause.append("IS NULL");
    } else if (fieldCondition.getOperator() == QueryOperator.NOT_EQUALS) {
      fragment.whereClause.append("IS NOT NULL");
    } else {
      handler.handle(Future
          .failedFuture(new NullPointerException("Invalid 'null' value for operator " + fieldCondition.getOperator())));
      return;
    }
    handler.handle(Future.succeededFuture(fragment));
  }

  /**
   * Creates the final fragment for a field condition, handling different parsed values like collections for IN queries,
   * or distance search
   * functions
   * 
   * @param fieldName
   *          the column field name of this clause
   * @param operator
   *          the parsed operator
   * @param valueResult
   *          the parsed value
   * @return the finished fragment of the condition
   */
  private SqlWhereFragment buildConditionFragment(String fieldName, String operator, Object valueResult) {
    SqlWhereFragment fragment = new SqlWhereFragment();
    if (valueResult instanceof SqlDistanceSearchFunction) {
      fragment.whereClause.append(" ").append(((SqlDistanceSearchFunction) valueResult).getFunctionSequence());
      fragment.parameters.add(((SqlDistanceSearchFunction) valueResult).getValue());
    } else {
      fragment.whereClause.append(fieldName).append(" ");
      fragment.whereClause.append(operator).append(" ");
      if (valueResult instanceof Collection<?>) {
        fragment.whereClause.append("(");
        Iterator<?> it = ((Collection<?>) valueResult).iterator();
        while (it.hasNext()) {
          fragment.whereClause.append("?");
          fragment.parameters.add(it.next());
          if (it.hasNext())
            fragment.whereClause.append(", ");
        }
        fragment.whereClause.append(")");
      } else {
        fragment.whereClause.append("?");
        fragment.parameters.add(valueResult);
      }
    }
    return fragment;
  }

  /**
   * Parses the query operator into native SQL format
   * 
   * @param operator
   * @return
   * @throws UnknownQueryOperatorException
   *           if the operator is unknown
   */
  private String translateOperator(QueryOperator operator) throws UnknownQueryOperatorException {
    switch (operator) {
    case EQUALS:
      return "=";
    case NOT_EQUALS:
      return "!=";
    case LARGER:
      return ">";
    case LARGER_EQUAL:
      return ">=";
    case SMALLER:
      return "<";
    case SMALLER_EQUAL:
    case NEAR:
      return "<=";
    case IN:
      return "IN";
    case NOT_IN:
      return "NOT IN";
    case STARTS:
    case ENDS:
    case CONTAINS:
      return "LIKE";
    default:
      throw new UnknownQueryOperatorException(operator);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#transformValue(java.lang.String,
   * de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator, java.lang.Object, io.vertx.core.Handler)
   */
  @Override
  protected void transformValue(IField field, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    super.transformValue(field, operator, value, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object transformedValue = result.result();
        switch (operator) {
        case CONTAINS:
          transformedValue = "%" + transformedValue + "%";
          break;
        case STARTS:
          transformedValue = transformedValue + "%";
          break;
        case ENDS:
          transformedValue = "%" + transformedValue;
          break;
        default:
          // noop
          break;
        }
        handler.handle(Future.succeededFuture(transformedValue));
      }
    });
  }

  /**
   * Parses the container part of a search condition. Loops through the content of the container and connects each
   * resulting condition with the specified connector.
   * 
   * @param container
   * @param handler
   */
  private void parseSearchConditionContainer(ISearchConditionContainer container,
      Handler<AsyncResult<SqlWhereFragment>> handler) {
    final String translatedConnector;
    try {
      translatedConnector = translateQueryLogic(container.getQueryLogic());
    } catch (QueryExpressionBuildException e) {
      handler.handle(Future.failedFuture(e));
      return;
    }

    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append("(");

    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (ISearchCondition searchCondition : container.getConditions()) {
      Future<SqlWhereFragment> future = Future.future();
      futures.add(future);
      internalBuildSearchCondition(searchCondition, future.completer());
    }

    CompositeFuture.all(futures).setHandler(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Iterator<Object> resultIterator = result.result().list().iterator();
        while (resultIterator.hasNext()) {
          SqlWhereFragment subFragment = (SqlWhereFragment) resultIterator.next();
          fragment.whereClause.append(subFragment.whereClause);
          fragment.parameters.addAll(subFragment.parameters);
          if (resultIterator.hasNext())
            fragment.whereClause.append(" ").append(translatedConnector).append(" ");
        }
        fragment.whereClause.append(")");
        handler.handle(Future.succeededFuture(fragment));
      }
    });
  }

  /**
   * Translate the logic connector into native SQL format
   * 
   * @param logic
   * @return the native SQL connector value
   * @throws UnknownQueryLogicException
   *           if the logic value is unknown
   */
  private String translateQueryLogic(QueryLogic logic) throws UnknownQueryLogicException {
    switch (logic) {
    case AND:
      return "AND";
    case OR:
      return "OR";
    default:
      throw new UnknownQueryLogicException(logic);
    }
  }

  /**
   * Checks if the query has parameters
   * 
   * @return true, iff the query has parameters
   */
  public boolean hasQueryParameters() {
    return parameters != null && !parameters.isEmpty();
  }

  /**
   * Get the parameters of the expression
   * 
   * @return
   */
  public JsonArray getParameters() {
    return parameters;
  }

  /**
   * Get the composite statement for a count
   * 
   * @return the complete expression
   */
  public String getCountExpression() {
    if (nativeCommand != null) {
      return nativeCommand;
    }
    StringBuilder countExpression = new StringBuilder(count);
    appendWhereClause(countExpression);
    return countExpression.toString();
  }

  /**
   * Get the composite select statement
   * 
   * @return the complete expression
   */
  public String getSelectExpression() {
    if (nativeCommand != null) {
      return nativeCommand;
    }
    StringBuilder selectExpression = new StringBuilder(select);
    appendWhereClause(selectExpression);
    appendOrderByClause(selectExpression);
    appendLimitClause(selectExpression);
    return selectExpression.toString();
  }

  /**
   * Get the composite delete statement
   * 
   * @return the complete expression
   */
  public String getDeleteExpression() {
    StringBuilder deleteExpression = new StringBuilder(delete);
    appendWhereClause(deleteExpression);
    return deleteExpression.toString();
  }

  /**
   * Add the limit and offset to the given expression, if they are defined
   * 
   * @param expression
   */
  private void appendLimitClause(StringBuilder expression) {
    if (getLimit() > 0) {
      expression.append(" LIMIT ").append(getLimit());
    }
    if (getOffset() > 0) {
      expression.append(" OFFSET ").append(getOffset());
    }
  }

  /**
   * Append the order by to the expression
   * 
   * @param expression
   */
  private void appendOrderByClause(StringBuilder expression) {
    if (orderByClause.length() > 0) {
      expression.append(" ORDER BY ").append(orderByClause);
    }
  }

  /**
   * Append the where condition to the expression
   * 
   * @param expression
   */
  private void appendWhereClause(StringBuilder expression) {
    if (whereClause.length() > 0) {
      expression.append(" WHERE ").append(whereClause);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ISortDefinition)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public IQueryExpression addSort(ISortDefinition<?> sortDef) {
    SortDefinition<?> sd = (SortDefinition<?>) sortDef;
    for (SortArgument sa : sd.getSortArguments()) {
      orderByClause.append(orderByClause.length() == 0 ? "" : ", ").append(sa.fieldName)
          .append(sa.ascending ? " asc" : " desc");
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getSelectExpression() + " | " + getDeleteExpression() + " | " + getParameters();
  }

  /**
   * POJO to hold the condition and optionally its parameters of a single fragment of the search condition
   */
  private static class SqlWhereFragment {
    private StringBuilder whereClause = new StringBuilder();
    private JsonArray parameters = new JsonArray();
  }
}
