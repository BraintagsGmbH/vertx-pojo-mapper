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
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import java.util.ArrayDeque;
import java.util.Deque;

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Mongo stores the query expression as JsonObject
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryExpression implements IQueryExpression {
  private JsonObject qDef = new JsonObject();
  private Object currentObject = qDef;
  private Deque<Object> deque = new ArrayDeque<>();
  private IMapper mapper;

  /**
   * 
   */
  public MongoQueryExpression() {
  }

  /**
   * Get the original Query definition for Mongo
   * 
   * @return
   */
  public JsonObject getQueryDefinition() {
    return qDef;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#startConnectorBlock(java.lang.String,
   * boolean)
   */
  @Override
  public IQueryExpression startConnectorBlock(String connector, boolean openParenthesis) {
    JsonArray array = new JsonArray();
    add(connector, array);
    deque.addLast(currentObject);
    currentObject = array;
    if (openParenthesis) {
      openParenthesis();
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#stopConnectorBlock()
   */
  @Override
  public IQueryExpression stopConnectorBlock() {
    currentObject = deque.pollLast();
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#openParenthesis()
   */
  @Override
  public IQueryExpression openParenthesis() {
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#closeParenthesis()
   */
  @Override
  public IQueryExpression closeParenthesis() {
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#addQuery(java.lang.String,
   * java.lang.String, java.lang.Object)
   */
  @Override
  public IQueryExpression addQuery(String fieldName, String logic, Object value) {
    JsonObject arg = new JsonObject().put(logic, value);
    add(fieldName, arg);
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setMapper(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper mapper) {
    this.mapper = mapper;
  }

  private void add(String key, Object objectToAdd) {
    if (currentObject instanceof JsonObject) {
      ((JsonObject) currentObject).put(key, objectToAdd);
    } else if (currentObject instanceof JsonArray) {
      JsonObject ob = new JsonObject().put(key, objectToAdd);
      ((JsonArray) currentObject).add(ob);
    } else
      throw new UnsupportedOperationException("no definition to add for " + currentObject.getClass().getName());
  }

  @Override
  public String toString() {
    return String.valueOf(qDef);
  }
}
