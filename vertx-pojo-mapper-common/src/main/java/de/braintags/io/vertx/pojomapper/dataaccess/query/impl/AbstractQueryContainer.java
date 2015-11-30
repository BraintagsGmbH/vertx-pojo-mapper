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
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;

/**
 * An abstract implementation of {@link IQueryContainer}
 * 
 * @author Michael Remme
 * @param <T>
 *          defines the class of the parent instance, which is an instance of IQueryContainer
 */

public abstract class AbstractQueryContainer<T extends IQueryContainer> implements IQueryContainer {
  private T parent;

  /**
   * Create an instance with the parent container, where this instance is belonging to
   * 
   * @param parent
   *          the parent container
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
  public IQuery getQuery() {
    IQueryContainer container = this;
    while (container != null) {
      if (container instanceof IQuery)
        return (IQuery<?>) container;
      container = (IQueryContainer) container.parent();
    }
    throw new NullPointerException("no absolute parent instance of IQuery found");
  }

}
