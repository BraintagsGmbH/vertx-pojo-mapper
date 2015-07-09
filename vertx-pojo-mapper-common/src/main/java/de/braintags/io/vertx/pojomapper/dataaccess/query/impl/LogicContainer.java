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

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class LogicContainer<T extends IQueryContainer> implements ILogicContainer<T> {
  private List<Object> filters = new ArrayList<Object>();
  private T parent;
  private QueryLogic logic;

  /**
   * 
   */
  public LogicContainer(T parent, QueryLogic logic) {
    this.parent = parent;
    this.logic = logic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#field(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<T>> field(String fieldName) {
    FieldParameter<LogicContainer<T>> param = new FieldParameter<LogicContainer<T>>(this, getMapper().getField(
        fieldName));
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

  @Override
  public T parent() {
    return parent;
  }

  /**
   * Fetch the mapper of the IQuery, which must be one parent or parent-parent etc.
   * 
   * @return
   */
  private IMapper getMapper() {
    ILogicContainer<?> container = this;
    while (container.parent() != null) {
      if (container.parent() instanceof ILogicContainer<?>) {
        container = (ILogicContainer<?>) container.parent();
      } else if (container instanceof IQuery<?>) {
        return ((IQuery<?>) container).getMapper();
      } else {
        throw new UnsupportedOperationException("unsupported parent: " + container.getClass().getName());
      }
    }
    return null;
  }
}
