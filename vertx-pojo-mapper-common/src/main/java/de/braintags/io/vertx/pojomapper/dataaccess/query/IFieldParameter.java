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
   * @return
   */
  T larger(Object value);

  /**
   * Defines a query argument where the value is larger or equal in the given field
   * 
   * @param value
   * @return
   */
  T largerEqual(Object value);

  /**
   * Defines a query argument where the value is less in the given field
   * 
   * @param value
   * @return
   */
  T less(Object value);

  /**
   * Defines a query argument where the value is less or equal in the given field
   * 
   * @param value
   * @return
   */
  T lessEqual(Object value);

  /**
   * Defines a query argument where the value is in the values in the given field
   * 
   * @param value
   * @return
   */
  T in(Object value);

  /**
   * Defines a query argument where the value is not in the values in the given field
   * 
   * @param value
   * @return
   */
  T notIn(Object value);

}
