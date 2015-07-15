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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;

/**
 * The IQueryRambler is used as argument by {@link Query#executeQueryRambler(IQueryRambler)}. This method traverses
 * through all parts of the query definition and calls the specified methods of this interface. An implementation will
 * use this interface to generate the native, database specific query object
 * 
 * @author Michael Remme
 * 
 */

public interface IQueryRambler {

  /**
   * Raises the hierarchy level of the container. The {@link IQuery} itself is level 0
   * 
   */
  void raiseLevel();

  /**
   * Reduces the hierarchy level of the container.
   */
  void reduceLevel();

  /**
   * Apply the {@link IQuery} itself
   * 
   * @param query
   *          the query to be applied
   */
  void apply(IQuery<?> query);

  /**
   * Apply an {@link ILogicContainer}
   * 
   * @param container
   *          the container to be applied
   */
  void apply(ILogicContainer<?> container);

  /**
   * Apply an {@link IFieldParameter}
   * 
   * @param fieldParameter
   *          the field paremeter to be applied
   */
  void apply(IFieldParameter<?> fieldParameter);

}
