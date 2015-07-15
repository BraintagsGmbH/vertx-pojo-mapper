/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler;
import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * Implementation fills the contents into a {@link JsonObject} which then can be used as source for
 * {@link MongoClient#find(String, JsonObject, io.vertx.core.Handler)}
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryRambler implements IQueryRambler {
  @SuppressWarnings("unused")
  private int level;
  private JsonObject qDef = new JsonObject();
  /**
   * This object switches during the processing queue to subobjects, which must be filled
   */
  private JsonObject currentJsonObject = qDef;

  /**
   * 
   */
  public MongoQueryRambler() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#raiseLevel()
   */
  @Override
  public void raiseLevel() {
    ++level;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#reduceLevel()
   */
  @Override
  public void reduceLevel() {
    --level;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IQuery)
   */
  @Override
  public void apply(IQuery<?> query) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.ILogicContainer)
   */
  @Override
  public void apply(ILogicContainer<?> container) {
    String logic = QueryLogicTranslator.translate(container.getLogic());

    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IFieldParameter)
   */
  @Override
  public void apply(IFieldParameter<?> fieldParameter) {
    IField field = fieldParameter.getField();
    String mongoOperator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object value = fieldParameter.getValue();
    Object storeObject = field.getTypeHandler().intoStore(value);
    JsonObject arg = new JsonObject().put(mongoOperator, storeObject);
    qDef.put(field.getMappedFieldName(), arg);
  }

  /**
   * @return the jsonObject
   */
  public JsonObject getJsonObject() {
    return qDef;
  }

}
