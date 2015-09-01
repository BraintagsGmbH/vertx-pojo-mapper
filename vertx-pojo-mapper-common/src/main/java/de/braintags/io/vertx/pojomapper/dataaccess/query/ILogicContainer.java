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
package de.braintags.io.vertx.pojomapper.dataaccess.query;

/**
 * a container to define AND / OR sequences
 * 
 * @author Michael Remme
 * 
 */

public interface ILogicContainer<T extends IQueryContainer> extends IQueryContainer {

  /**
   * Get the {@link QueryLogic} of the current definition
   * 
   * @return the logic
   */
  public QueryLogic getLogic();

  /**
   * Retrive the parent instance, which contains the current instance
   * 
   * @return the parent
   */
  @Override
  public T parent();

}
