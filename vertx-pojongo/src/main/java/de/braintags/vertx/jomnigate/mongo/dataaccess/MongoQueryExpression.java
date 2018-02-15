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
package de.braintags.vertx.jomnigate.mongo.dataaccess;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer;
import de.braintags.vertx.jomnigate.dataaccess.query.ISortDefinition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryLogic;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.InvalidQueryValueException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.SortDefinition;
import de.braintags.vertx.util.json.JsonConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
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
  private JsonObject fields;

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
    if (fields != null) {
      findOptions.setFields(fields);
    }
    return findOptions;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#handleFinishedBuild(java.lang.
   * Object)
   */
  @Override
  protected void handleFinishedSearchCondition(final JsonObject result) {
    this.searchCondition = result;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#parseContainerContents(java.util.
   * List, de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer)
   */
  @Override
  protected JsonObject parseContainerContents(final List<JsonObject> parsedConditionList,
      final ISearchConditionContainer container) throws UnknownQueryLogicException {
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
  private String translateQueryLogic(final QueryLogic logic) throws UnknownQueryLogicException {
    switch (logic) {
      case AND:
        return "$and";
      case OR:
        return "$or";
      case NOT:
        return "$nor";
      default:
        throw new UnknownQueryLogicException(logic);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#buildFieldConditionResult(de.braintags.
   * vertx.jomnigate.dataaccess.query.IFieldCondition, java.lang.String, java.lang.Object)
   */
  @Override
  protected JsonObject buildFieldConditionResult(final IFieldCondition fieldCondition, final String columnName,
      final JsonNode value) throws UnknownQueryOperatorException, InvalidQueryValueException {
    QueryOperator operator = fieldCondition.getOperator();
    JsonNode parsedValue;
    switch (operator) {
      case EQUALS_IGNORE_CASE:
        parsedValue = new TextNode(createEqualsRegex(value));
        break;
      case CONTAINS:
        parsedValue = new TextNode(Pattern.quote(value.textValue()));
        break;
      case STARTS:
        parsedValue = new TextNode("^" + Pattern.quote(value.textValue()));
        break;
      case ENDS:
        parsedValue = new TextNode(Pattern.quote(value.textValue()) + "$");
        break;
      case IN_IGNORE_CASE:
        if (value.isArray()) {
          ArrayNode arrayNode = Json.mapper.getNodeFactory().arrayNode();
          for (Iterator<JsonNode> iterator = ((ArrayNode) value).elements(); iterator.hasNext();) {
            JsonNode node = iterator.next();
            arrayNode.add(new TextNode(createEqualsRegex(node)));
          }
          parsedValue = arrayNode;
        } else {
          parsedValue = new TextNode(createEqualsRegex(value));
        }

        break;
      default:
        parsedValue = value;
        break;
    }

    String parsedOperator = translateOperator(operator);
    JsonObject logicCondition = new JsonObject();
    try {
      logicCondition.put(parsedOperator, JsonConverter.convertJsonNodeToVertx(parsedValue));
    } catch (IOException e) {
      throw new InvalidQueryValueException(e);
    }
    // make RegEx comparisons case insensitive
    if (operator == QueryOperator.EQUALS_IGNORE_CASE || operator == QueryOperator.CONTAINS
        || operator == QueryOperator.STARTS || operator == QueryOperator.ENDS
        || operator == QueryOperator.IN_IGNORE_CASE)
      logicCondition.put("$options", "i");

    JsonObject expression = new JsonObject();
    expression.put(columnName, logicCondition);
    return expression;
  }

  private String createEqualsRegex(final JsonNode value) {
    return "^" + Pattern.quote(value.textValue()) + "$";
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#handleNullConditionValue(de.braintags.
   * vertx.jomnigate.dataaccess.query.IFieldCondition, java.lang.String, io.vertx.core.Handler)
   */
  @Override
  protected void handleNullConditionValue(final IFieldCondition condition, final String columnName,
      final Handler<AsyncResult<JsonObject>> handler) {
    // special case for query with null value
    if (condition.getOperator() == QueryOperator.EQUALS || condition.getOperator() == QueryOperator.EQUALS_IGNORE_CASE
        || condition.getOperator() == QueryOperator.NOT_EQUALS) {
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
  private String translateOperator(final QueryOperator operator) throws UnknownQueryOperatorException {
    switch (operator) {
      case EQUALS:
        return "$eq";
      case IN_IGNORE_CASE:
      case EQUALS_IGNORE_CASE:
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
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.vertx.jomnigate.
   * dataaccess.query.ISortDefinition)
   */
  @Override
  public IQueryExpression addSort(final ISortDefinition<?> sortDef) {
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
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(final Object nativeCommand) {
    if (nativeCommand instanceof JsonObject) {
      searchCondition = (JsonObject) nativeCommand;
    } else if (nativeCommand instanceof CharSequence) {
      searchCondition = new JsonObject(nativeCommand.toString());
    } else {
      throw new UnsupportedOperationException("Can not create a native command from an object of class: "
          + (nativeCommand != null ? nativeCommand.getClass() : "null"));
    }
  }

  @Override
  public void setUseFields(final List<String> useFields) {
    if (useFields != null && !useFields.isEmpty()) {
      fields = new JsonObject();
      useFields.stream().forEach(useField -> fields.put(useField, 1));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(searchCondition) + " | sort: " + sortArguments + " | limit: " + getOffset() + "/" + getLimit()
        + " | fields: " + fields;
  }

}
