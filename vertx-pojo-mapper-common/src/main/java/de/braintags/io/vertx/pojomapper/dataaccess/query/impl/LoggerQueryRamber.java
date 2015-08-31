/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;

/**
 * An implementation of IQueryRambler which is logging the elements
 * 
 * @author Michael Remme
 * 
 */

public class LoggerQueryRamber implements IQueryRambler {
  private static Logger logger = LoggerFactory.getLogger(LoggerQueryRamber.class);
  private String levelPrefix = "";
  private int level;

  /**
   * 
   */
  public LoggerQueryRamber() {
  }

  public void raiseLevel() {
    ++level;
    setHirarchyString();
  }

  public void reduceLevel() {
    --level;
    setHirarchyString();
  }

  public void setHirarchyString() {
    StringBuffer prefixBuffer = new StringBuffer();
    for (int i = 0; i < level; i++) {
      prefixBuffer.append(" ");
    }
    levelPrefix = prefixBuffer.toString();
  }

  @Override
  public void start(IQuery<?> query) {
    log("start query in: " + query.getMapper().getDataStoreName());
  }

  @Override
  public void stop(IQuery<?> query) {
    log("stop query ");
  }

  @Override
  public void start(ILogicContainer<?> container) {
    raiseLevel();
    log(container.getLogic().toString());
  }

  @Override
  public void stop(ILogicContainer<?> container) {
    reduceLevel();
  }

  @Override
  public void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    raiseLevel();
    log(fieldParameter.getField().getName() + " " + fieldParameter.getOperator().toString() + " "
        + fieldParameter.getValue());
    resultHandler.handle(Future.succeededFuture());
  }

  @Override
  public void stop(IFieldParameter<?> fieldParameter) {
    reduceLevel();
  }

  private final void log(String message) {
    logger.info(levelPrefix + message);
  }

}
