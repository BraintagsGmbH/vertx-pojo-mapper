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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractQueryContainer<T extends IQueryContainer> implements IQueryContainer {
  private T parent;

  /**
   * 
   */
  public AbstractQueryContainer(T parent) {
    this.parent = parent;
  }

  @Override
  public T parent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getQuery()
   */
  public IQuery<?> getQuery() {
    IQueryContainer container = this;
    while (container != null) {
      if (container instanceof IQuery)
        return (IQuery<?>) container;
      container = (IQueryContainer) container.parent();
    }
    throw new NullPointerException("no absolute parent instance of IQuery found");
  }

}
