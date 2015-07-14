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

package de.braintags.io.vertx.pojomapper.dataaccess.query;

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
   * Get the IQuery, where the current instance is part from
   * 
   * @return the query
   */
  public IQuery<?> getQuery();

}