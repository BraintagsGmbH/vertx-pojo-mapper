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

import java.util.List;

/**
 * A general description of a container, which will contain different search definitions
 * 
 * @author Michael Remme
 * 
 */

public interface IQueryContainer {

  /**
   * Add a query for a specified field
   * 
   * @param fieldName
   *          the name of the field
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends IQueryContainer> field(String fieldName);

  /**
   * Start an "and" sequence
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> and(String fieldName);

  /**
   * Start an "or" sequence
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> or(String fieldName);

  /**
   * Get the parent instance of the current object
   * 
   * @return
   */
  public Object parent();

  /**
   * Get the child definitions
   * 
   * @return the children
   */
  public List<Object> getChildren();

}
