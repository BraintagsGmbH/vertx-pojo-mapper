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

import java.util.Set;

import de.braintags.io.vertx.pojomapper.annotation.ObjectFactory;

/**
 * IMapper is responsible to collect information about the mapped class
 * 
 * @author Michael Remme
 * 
 */

public interface IMapper {

  /**
   * Get the {@link IObjectFactory} which is defined for the current mapper. To define the {@link IObjectFactory} for
   * the class, you will use the annotation {@link ObjectFactory}
   * 
   * @return the {@link IObjectFactory} to be used
   */
  IObjectFactory getObjectFactory();

  /**
   * Get a list of fieldnames which are handled by the current mapper
   * 
   * @return the mapped fieldnames
   */
  public Set<String> getFieldNames();

  /**
   * Get the {@link IField} as a descriptor for the given field name
   * 
   * @param name
   *          the name of the field
   * @return an instance of {@link IField} or null, if field does not exist
   */
  public IField getField(String name);
}
