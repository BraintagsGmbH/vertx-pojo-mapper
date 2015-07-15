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

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#raiseHirarchyLevel()
   */
  @Override
  public void raiseLevel() {
    ++level;
    setHirarchyString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#reduceLevel()
   */
  @Override
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#apply(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IQuery)
   */
  @Override
  public void apply(IQuery<?> query) {
    log("query in: " + query.getMapper().getDataStoreName());
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
    log(container.getLogic().toString());
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
    log(fieldParameter.getField().getName() + " " + fieldParameter.getOperator().toString() + " "
        + fieldParameter.getValue());
  }

  private final void log(String message) {
    logger.info(levelPrefix + message);
  }

}
