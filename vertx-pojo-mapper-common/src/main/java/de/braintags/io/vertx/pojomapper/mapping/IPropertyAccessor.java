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

package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.exception.PropertyAccessException;

/**
 * An accessor to a property within a Java class. The exact location and access of the property is left to the
 * implementation class.
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyAccessor {

  /**
   * The name of the underlaying property
   * 
   * @return the name
   */
  public String getName();

  /**
   * Reads the content from the given object
   * 
   * @param record
   *          the record from which to read the content
   * @return the content read
   * @exception PropertyAccessException
   *              thrown if the property cannot be accessed
   */
  Object readData(Object record);

  /**
   * Writes the content into the given record
   * 
   * @param record
   *          the record from which to read the content
   * @param data
   *          the data to be written
   * @exception PropertyAccessException
   *              thrown if the property cannot be accessed
   */
  void writeData(Object record, Object data);

}
