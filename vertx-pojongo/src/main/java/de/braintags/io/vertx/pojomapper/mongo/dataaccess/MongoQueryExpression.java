/*
 * #%L vertx-pojongo %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryPartException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Mongo stores the query expression as JsonObject
 * 
 * @author Michael Remme
 */

public class MongoQueryExpression extends AbstractQueryExpression {
  private JsonObject searchCondition = new JsonObject();
  private JsonObject sortArguments;

  /**
   * Get the original Query definition for Mongo
   * 
   * @return
   */
  public JsonObject getQueryDefinition() {
    return searchCondition;
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
    internalBuildQuery(queryPart, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        JsonObject expression = result.result();
        this.searchCondition = expression;
        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void internalBuildQuery(IQueryPart queryPart, Handler<AsyncResult<JsonObject>> handler) {
    if (queryPart instanceof IQueryCondition) {
      parseQueryCondition((IQueryCondition) queryPart, handler);
    } else if (queryPart instanceof IQueryContainer) {
      parseQueryContainer((IQueryContainer) queryPart, handler);
    } else {
      handler.handle(Future.failedFuture(new UnknownQueryPartException(queryPart)));
    }
  }

  private void parseQueryContainer(IQueryContainer container, Handler<AsyncResult<JsonObject>> handler) {
    String connector;
    try {
      connector = translateConnector(container.getConnector());
    } catch (UnknownQueryLogicException e) {
      handler.handle(Future.failedFuture(e));
      return;
    }
    List<IQueryPart> content = container.getContent();
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (IQueryPart queryPart : content) {
      Future<JsonObject> future = Future.future();
      futures.add(future);
      internalBuildQuery(queryPart, future.completer());
    }

    CompositeFuture.all(futures).setHandler(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<Object> results = result.result().list();
        JsonArray subExpressions = new JsonArray(results);
        JsonObject expression = new JsonObject();
        expression.put(connector, subExpressions);
        handler.handle(Future.succeededFuture(expression));
      }
    });
  }

  /**
   * @param connector
   * @return
   * @throws UnknownQueryLogicException
   */
  private String translateConnector(QueryLogic connector) throws UnknownQueryLogicException {
    switch (connector) {
    case AND:
      return "$and";
    case OR:
      return "$or";
    default:
      throw new UnknownQueryLogicException(connector);
    }
  }

  private void parseQueryCondition(IQueryCondition condition, Handler<AsyncResult<JsonObject>> handler) {

    String parsedLogic;
    try {
      parsedLogic = translateOperator(condition.getOperator());
    } catch (UnknownQueryOperatorException e) {
      handler.handle(Future.failedFuture(e));
      return;
    }

    if (condition.getValue() != null) {
      transformValue(condition.getField(), condition.getOperator(), condition.getValue(), result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          Object parsedValue = result.result();
          JsonObject logicCondition = new JsonObject();
          logicCondition.put(parsedLogic, parsedValue);

          JsonObject expression = new JsonObject();
          expression.put(condition.getField(), logicCondition);
          handler.handle(Future.succeededFuture(expression));
        }
      });
    } else {
      if (condition.getOperator() == QueryOperator.EQUALS || condition.getOperator() == QueryOperator.NOT_EQUALS) {
        JsonObject expression = new JsonObject();
        JsonObject logicCondition = new JsonObject();
        logicCondition.putNull(parsedLogic);
        expression.put(condition.getField(), logicCondition);
        handler.handle(Future.succeededFuture(expression));
      } else {
        handler.handle(Future
            .failedFuture(new NullPointerException("Invalid 'null' value for operator " + condition.getOperator())));
        return;
      }
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
          transformedValue = "." + transformedValue + ".";
          break;
        case STARTS:
          transformedValue = transformedValue + ".";
          break;
        case ENDS:
          transformedValue = "." + transformedValue;
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
   * @param operator
   * @return
   * @throws UnknownQueryOperatorException
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

  @Override
  public String toString() {
    return String.valueOf(searchCondition) + " | sort: " + String.valueOf(sortArguments);
  }

}
