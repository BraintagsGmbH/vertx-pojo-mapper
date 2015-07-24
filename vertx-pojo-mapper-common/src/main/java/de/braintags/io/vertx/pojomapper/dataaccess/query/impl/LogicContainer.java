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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class LogicContainer<T extends IQueryContainer> extends AbstractQueryContainer<IQueryContainer> implements
    ILogicContainer<T>, IRamblerSource {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(LogicContainer.class);
  private List<Object> filters = new ArrayList<Object>();
  // private T parent;
  private QueryLogic logic;

  /**
   * 
   */
  public LogicContainer(T parent, QueryLogic logic) {
    super(parent);
    this.logic = logic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#field(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<T>> field(String fieldName) {
    FieldParameter<LogicContainer<T>> param = new FieldParameter<LogicContainer<T>>(this, getQuery().getMapper()
        .getField(fieldName));
    filters.add(param);
    return param;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#and(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<IQueryContainer>> and(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.AND);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#or(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<IQueryContainer>> or(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.OR);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer#getLogic()
   */
  @Override
  public QueryLogic getLogic() {
    return logic;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T parent() {
    return (T) super.parent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IRamblerSource#applyTo(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.impl.IQueryRambler)
   */
  @Override
  public void applyTo(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.start(this);
    ErrorObject<Void> error = new ErrorObject<Void>();
    for (Object filter : filters) {
      if (filter instanceof IRamblerSource) {
        ((IRamblerSource) filter).applyTo(rambler, result -> {
          if (result.failed()) {
            error.setThrowable(result.cause());
            resultHandler.handle(result);
          } else {
            // nothing to do here
          }
        });
        if (error.isError()) {
          return;
        }
      } else {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException(
            "NOT AN INSTANCE OF IRamblerSource: " + filter.getClass().getName())));
        return;
      }
    }
    rambler.stop(this);
    resultHandler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getChildren()
   */
  @Override
  public List<Object> getChildren() {
    return filters;
  }

}
