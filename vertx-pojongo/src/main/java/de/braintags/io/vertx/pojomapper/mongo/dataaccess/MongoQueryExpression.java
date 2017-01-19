/*-
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;

/**
 * Mongo stores the query expression as JsonObject
 *
 * @author Michael Remme
 */

public class MongoQueryExpression extends AbstractQueryExpression<JsonObject> {
  private JsonObject searchCondition = new JsonObject();
  private JsonObject sortArguments;

  /**
   * Get the native query definition for Mongo
   *
   * @return
   */
  public JsonObject getQueryDefinition() {
    return searchCondition;
  }

  /**
   * Creates the FindOptions to set the skip, limit, and sort parameters for a find operation
   *
   * @return the find options for this expression
   */
  public FindOptions getFindOptions() {
    FindOptions findOptions = new FindOptions();
    if (getLimit() > 0)
      findOptions.setLimit(getLimit());
    if (getOffset() > 0)
      findOptions.setSkip(getOffset());
    if (getSortArguments() != null && !getSortArguments().isEmpty()) {
      findOptions.setSort(getSortArguments());
    }
    return findOptions;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#handleFinishedBuild(java.lang.
   * Object)
   */
  @Override
  protected void handleFinishedSearchCondition(JsonObject result) {
    this.searchCondition = result;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#parseContainerContents(java.util.
   * List, de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer)
   */
  @Override
  protected JsonObject parseContainerContents(List<JsonObject> parsedConditionList, ISearchConditionContainer container)
      throws UnknownQueryLogicException {
    String queryLogic = translateQueryLogic(container.getQueryLogic());
    JsonArray subExpressions = new JsonArray(parsedConditionList);
    JsonObject expression = new JsonObject();
    expression.put(queryLogic, subExpressions);
    return expression;
  }

  /**
   * Translate the logic connector into native MongoDB format
   *
   * @param logic
   * @return the native MongoDB connector key
   * @throws UnknownQueryLogicException
   *           if the logic value is unknown
   */
  private String translateQueryLogic(QueryLogic logic) throws UnknownQueryLogicException {
    switch (logic) {
    case AND:
      return "$and";
    case OR:
      return "$or";
    default:
      throw new UnknownQueryLogicException(logic);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#buildFieldConditionResult(de.
   * braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition, java.lang.String, java.lang.Object)
   */
  @Override
  protected JsonObject buildFieldConditionResult(IFieldCondition fieldCondition, String columnName, Object parsedValue)
      throws UnknownQueryOperatorException {
    QueryOperator operator = fieldCondition.getOperator();

    switch (operator) {
    case CONTAINS:
      parsedValue = ".*" + parsedValue + ".*";
      break;
    case STARTS:
      parsedValue = parsedValue + ".*";
      break;
    case ENDS:
      parsedValue = ".*" + parsedValue;
      break;
    default:
      // noop
      break;
    }

    String parsedOperator = translateOperator(operator);
    JsonObject logicCondition = new JsonObject();
    logicCondition.put(parsedOperator, parsedValue);
    // make RegEx comparisons case insensitive
    if (operator == QueryOperator.CONTAINS || operator == QueryOperator.STARTS || operator == QueryOperator.ENDS)
      logicCondition.put("$options", "i");

    JsonObject expression = new JsonObject();
    expression.put(columnName, logicCondition);
    return expression;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#handleNullConditionValue(de.
   * braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition, java.lang.String, io.vertx.core.Handler)
   */
  @Override
  protected void handleNullConditionValue(IFieldCondition condition, String columnName,
      Handler<AsyncResult<JsonObject>> handler) {
    // special case for query with null value
    if (condition.getOperator() == QueryOperator.EQUALS || condition.getOperator() == QueryOperator.NOT_EQUALS) {
      String parsedLogic;
      try {
        parsedLogic = translateOperator(condition.getOperator());
      } catch (UnknownQueryOperatorException e) {
        handler.handle(Future.failedFuture(e));
        return;
      }
      JsonObject expression = new JsonObject();
      JsonObject logicCondition = new JsonObject();
      logicCondition.putNull(parsedLogic);
      expression.put(columnName, logicCondition);
      handler.handle(Future.succeededFuture(expression));
    } else {
      handler.handle(Future
          .failedFuture(new NullPointerException("Invalid 'null' value for operator " + condition.getOperator())));
      return;
    }
  }

  /**
   * Translate the query operator to the native MongoDB value
   *
   * @param operator
   * @return
   * @throws UnknownQueryOperatorException
   *           if the operator is unknown
   */
  private String translateOperator(QueryOperator operator) throws UnknownQueryOperatorException {
    switch (operator) {
    case EQUALS:
      return "$eq";
    case CONTAINS:
    case STARTS:
    case ENDS:
      return "$regex";
    case NOT_EQUALS:
      return "$ne";
    case LARGER:
      return "$gt";
    case LARGER_EQUAL:
      return "$gte";
    case SMALLER:
      return "$lt";
    case SMALLER_EQUAL:
      return "$lte";
    case IN:
      return "$in";
    case NOT_IN:
      return "$nin";
    case NEAR:
      return "$geoNear";
    default:
      throw new UnknownQueryOperatorException(operator);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ISortDefinition)
   */
  @Override
  public IQueryExpression addSort(ISortDefinition<?> sortDef) {
    SortDefinition<?> sd = (SortDefinition<?>) sortDef;
    if (!sd.getSortArguments().isEmpty()) {
      sortArguments = new JsonObject();
      sd.getSortArguments().forEach(sda -> sortArguments.put(sda.fieldName, sda.ascending ? 1 : -1));
    }
    return this;
  }

  /**
   * Get the sort arguments, which were created by method {@link #addSort(ISortDefinition)}
   *
   * @return the sortArguments or null, if none
   */
  public final JsonObject getSortArguments() {
    return sortArguments;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object nativeCommand) {
    if (nativeCommand instanceof JsonObject) {
      searchCondition = (JsonObject) nativeCommand;
    } else if (nativeCommand instanceof CharSequence) {
      searchCondition = new JsonObject(nativeCommand.toString());
    } else {
      throw new UnsupportedOperationException("Can not create a native command from an object of class: "
          + (nativeCommand != null ? nativeCommand.getClass() : "null"));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(searchCondition) + " | sort: " + sortArguments + " | limit: " + getOffset() + "/"
        + getLimit();
  }

}
