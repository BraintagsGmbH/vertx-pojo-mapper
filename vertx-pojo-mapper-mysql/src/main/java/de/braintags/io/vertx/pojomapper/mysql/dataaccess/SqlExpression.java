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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.QueryExpressionBuildException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryPartException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition.SortArgument;
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
   * pojomapper.dataaccess.query.IQueryPart)
   */
  @Override
  public void buildQueryExpression(IQueryPart queryPart, Handler<AsyncResult<Void>> handler) {
    internalBuildSearchCondition(queryPart, result -> {
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
   * Recursive method to build the query parts into the native search condition of the query
   * 
   * @param queryPart
   * @param handler
   */
  private void internalBuildSearchCondition(IQueryPart queryPart, Handler<AsyncResult<SqlWhereFragment>> handler) {
    if (queryPart instanceof IQueryCondition) {
      parseQueryCondition((IQueryCondition) queryPart, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          handler.handle(Future.succeededFuture(result.result()));
        }
      });
    } else if (queryPart instanceof IQueryContainer) {
      parseQueryContainer((IQueryContainer) queryPart, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          handler.handle(Future.succeededFuture(result.result()));
        }
      });
    } else {
      handler.handle(Future.failedFuture(new UnknownQueryPartException(queryPart)));
    }
  }

  /**
   * Parses an {@link IQueryCondition}. The operator and value will be transformed into a format fitting the SQL
   * database
   * 
   * @param queryCondition
   * @param handler
   */
  private void parseQueryCondition(IQueryCondition queryCondition, Handler<AsyncResult<SqlWhereFragment>> handler) {
    if (queryCondition.getValue() != null) {
      transformValue(queryCondition.getField(), queryCondition.getOperator(), queryCondition.getValue(), result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          Object parsedValue = result.result();
          String parsedOperator;
          try {
            parsedOperator = transformOperator(queryCondition.getOperator());
          } catch (UnknownQueryOperatorException e) {
            handler.handle(Future.failedFuture(e));
            return;
          }
          SqlWhereFragment fragment = handleParsedCondition(queryCondition.getField(), parsedOperator, parsedValue);
          handler.handle(Future.succeededFuture(fragment));
        }
      });
    } else {
      // special handling for NULL values
      SqlWhereFragment fragment = new SqlWhereFragment();
      fragment.whereClause.append(queryCondition.getField()).append(" ");
      if (queryCondition.getOperator() == QueryOperator.EQUALS) {
        fragment.whereClause.append("IS NULL");
      } else if (queryCondition.getOperator() == QueryOperator.NOT_EQUALS) {
        fragment.whereClause.append("IS NOT NULL");
      } else {
        handler.handle(Future.failedFuture(
            new NullPointerException("Invalid 'null' value for operator " + queryCondition.getOperator())));
        return;
      }
      handler.handle(Future.succeededFuture(fragment));
    }
  }

  /**
   * Creates the final clause, handling different parsed values like collections for IN queries, or distance search
   * functions
   * 
   * @param field
   *          the field of this clause
   * @param operator
   *          the parsed operator
   * @param valueResult
   *          the parsed value
   * @return
   */
  private SqlWhereFragment handleParsedCondition(String field, String operator, Object valueResult) {
    SqlWhereFragment fragment = new SqlWhereFragment();
    if (valueResult instanceof SqlDistanceSearchFunction) {
      fragment.whereClause.append(" ").append(((SqlDistanceSearchFunction) valueResult).getFunctionSequence());
      fragment.parameters.add(((SqlDistanceSearchFunction) valueResult).getValue());
    } else {
      fragment.whereClause.append(field).append(" ");
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
   */
  private String transformOperator(QueryOperator operator) throws UnknownQueryOperatorException {
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
  protected void transformValue(String fieldName, QueryOperator operator, Object value,
      Handler<AsyncResult<Object>> handler) {
    super.transformValue(fieldName, operator, value, result -> {
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
  private void parseQueryContainer(IQueryContainer container, Handler<AsyncResult<SqlWhereFragment>> handler) {
    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append("(");

    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (IQueryPart queryPart : container.getContent()) {
      Future<SqlWhereFragment> future = Future.future();
      futures.add(future);
      internalBuildSearchCondition(queryPart, future.completer());
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
            try {
              fragment.whereClause.append(" ").append(translateConnector(container.getConnector())).append(" ");
            } catch (QueryExpressionBuildException e) {
              handler.handle(Future.failedFuture(e));
              return;
            }
        }
        fragment.whereClause.append(")");
        handler.handle(Future.succeededFuture(fragment));
      }
    });
  }

  /**
   * Translate the connector into native SQL format
   * 
   * @param connector
   * @return the native SQL connector value
   * @throws QueryExpressionBuildException
   */
  private String translateConnector(QueryLogic connector) throws QueryExpressionBuildException {
    switch (connector) {
    case AND:
      return "AND";
    case OR:
      return "OR";
    default:
      throw new UnknownQueryLogicException(connector);
    }
  }

  /**
   * Get the information, whether the query has parameters
   * 
   * @return true, if query has parameters
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
    StringBuilder complete = new StringBuilder(count);
    appendWhereClause(complete);
    return complete.toString();
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
    StringBuilder complete = new StringBuilder(select);
    appendWhereClause(complete);
    appendOrderByClause(complete);
    appendLimitClause(complete);
    return complete.toString();
  }

  /**
   * Get the composite delete statement
   * 
   * @return the complete expression
   */
  public String getDeleteExpression() {
    StringBuilder complete = new StringBuilder(delete);
    appendWhereClause(complete);
    return complete.toString();
  }

  private void appendLimitClause(StringBuilder complete) {
    if (getLimit() > 0) {
      complete.append(" LIMIT ").append(getLimit());
    }
    if (getOffset() > 0) {
      complete.append(" OFFSET ").append(getOffset());
    }
  }

  private void appendOrderByClause(StringBuilder complete) {
    if (orderByClause.length() > 0) {
      complete.append(" ORDER BY ").append(orderByClause);
    }
  }

  private void appendWhereClause(StringBuilder complete) {
    if (whereClause.length() > 0) {
      complete.append(" WHERE ").append(whereClause);
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
