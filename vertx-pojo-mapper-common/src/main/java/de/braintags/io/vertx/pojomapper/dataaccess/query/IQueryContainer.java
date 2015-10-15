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
   * Start an "and" sequence. The "and" is used, until an OR-connector is added
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> and(String fieldName);

  /**
   * Start an "and" sequence and open a parenthesis "(". The "and" is used, until an OR-connector is added
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> andOpen(String fieldName);

  /**
   * Start an "or" sequence; the "or" is used, until an "and" is started
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> or(String fieldName);

  /**
   * Start an "or" sequence and open a parenthesis "("; the "or" is used, until an "and" is started
   * 
   * @param fieldName
   *          the name of the field to search for
   * @return an instance of {@link IFieldParameter}
   */
  IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> orOpen(String fieldName);

  /**
   * Closes a previously opened parenthesis which was opened by {@link #orOpen(String)} or {@link #andOpen(String)}
   * 
   * @return the container itself
   */
  IQueryContainer close();

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
