/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

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
    StringBuilder prefixBuffer = new StringBuilder();
    for (int i = 0; i < level; i++) {
      prefixBuffer.append(" ");
    }
    levelPrefix = prefixBuffer.toString();
  }

  @Override
  public void start(IQuery<?> query) {
    log("start query in: " + query.getMapper().getTableInfo().getName());
  }

  @Override
  public void stop(IQuery<?> query) {
    log("stop query ");
  }

  private final void log(String message) {
    logger.info(levelPrefix + message);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.IQueryPart, io.vertx.core.Handler)
   */
  @Override
  public void apply(IQueryPart queryPart, Handler<AsyncResult<Void>> resultHandler) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition, io.vertx.core.Handler)
   */
  @Override
  public void apply(ISortDefinition<?> sortDefinition, Handler<AsyncResult<Void>> resultHandler) {
  }

}
