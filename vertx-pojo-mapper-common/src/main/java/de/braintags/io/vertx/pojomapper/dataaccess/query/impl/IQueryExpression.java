/*
 * #%L
 * vertx-pojongo
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

import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * IQueryExpression is the datastore specific result of an executed {@link AbstractQueryRambler}, which later on is used
 * to execute the query on the datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IQueryExpression {

  /**
   * Start an AND / OR block
   * 
   * @param connector
   *          the connector AND / OR
   * @param openParenthesis
   *          info, wether a parenthesis shall be opened
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression startConnectorBlock(String connector, boolean openParenthesis);

  /**
   * Stop the current connector block
   * 
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression stopConnectorBlock();

  /**
   * Append an opening parenthesis and handle the counter for open parenthesis
   * 
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression openParenthesis();

  /**
   * Append a closing parenthesis and handle the counter for open parenthesis
   * 
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression closeParenthesis();

  /**
   * Set a native command to be executed as query
   * 
   * @param nativeCommand
   *          the command to be set
   */
  void setNativeCommand(Object nativeCommand);

  /**
   * add a query expression
   * 
   * @param fieldName
   *          the name to search in
   * @param logic
   *          the logic
   * @param value
   *          the value
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression addQuery(String fieldName, String logic, Object value);

  /**
   * Set the {@link IMapper} to be used
   * 
   * @param mapper
   *          the mapper
   */
  void setMapper(IMapper mapper);

  /**
   * Adds the given {@link ISortDefinition} into the current instance like it is needed by the implementation
   * 
   * @param sortDef
   *          the sort definition to be added
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression addSort(ISortDefinition<?> sortDef);

}
