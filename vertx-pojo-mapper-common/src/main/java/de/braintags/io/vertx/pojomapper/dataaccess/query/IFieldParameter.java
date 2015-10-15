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

import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * Defines the logic of the field arguments of a query
 * 
 * @author Michael Remme
 * 
 */

public interface IFieldParameter<T extends IQueryContainer> {

  /**
   * Get the underlaying {@link IField} of the current definition
   * 
   * @return the field
   */
  IField getField();

  /**
   * Get the underlaying {@link QueryOperator}
   * 
   * @return the operator
   */
  QueryOperator getOperator();

  /**
   * Get the underlaying value
   * 
   * @return the value
   */
  Object getValue();

  /**
   * Defines a query argument which fits exact the given argumentT
   * 
   * @param value
   *          the value to search for
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T is(Object value);

  /**
   * Defines a query argument where the value is not equal in the given field
   * 
   * @param value
   *          the value to search for
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T isNot(Object value);

  /**
   * Defines a query argument where the value is larger in the given field
   * 
   * @param value
   *          the value to be checked
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T larger(Object value);

  /**
   * Defines a query argument where the value is larger or equal in the given field
   * 
   * @param value
   *          the value to be checked
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T largerEqual(Object value);

  /**
   * Defines a query argument where the value is less in the given field
   * 
   * @param value
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T less(Object value);

  /**
   * Defines a query argument where the value is less or equal in the given field
   * 
   * @param value
   *          the value to be checked
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T lessEqual(Object value);

  /**
   * Defines a query argument where the value is in the values in the given field
   * 
   * @param value
   *          values to be checked as {@link Iterable}
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T in(Iterable<?> value);

  /**
   * Defines a query argument where the value is in the values in the given field
   * 
   * @param value
   *          values to be checked as array
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T in(Object... values);

  /**
   * Defines a query argument where the value is not in the values
   * 
   * @param value
   *          values to be checked as {@link Iterable}
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T notIn(Iterable<?> value);

  /**
   * Defines a query argument where the value is not in the values
   * 
   * @param value
   *          values to be checked as array
   * @return the parent {@link IQueryContainer} to enable chaining of commands
   */
  T notIn(Object... values);

  /**
   * Get the information, wether after this field a parenthesis shall be closed. A closing parenthesis is set by
   * {@link ILogicContainer#close()}
   * 
   * @return true, if parenthesis shall be closed
   */
  boolean isCloseParenthesis();

  /**
   * Set the information, whether after this field sequence a closing parenthesis shall be placed. A closing parenthesis
   * is set by {@link ILogicContainer#close()}
   * 
   * @param doClose
   *          true, if close is required
   */
  void setCloseParenthesis(boolean doClose);
}
