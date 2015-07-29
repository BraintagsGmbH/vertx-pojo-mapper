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

import java.util.Collection;
import java.util.Map;

/**
 * IObjectFactory is used to create new instances for a mapper.
 * 
 * @author Michael Remme
 * 
 */

public interface IObjectFactory {

  /**
   * Creates an instance of the given class.
   * 
   * @param clazz
   * @return
   */
  <T> T createInstance(Class<T> clazz);

  /**
   * Create a new instance of {@link Collection}
   * 
   * @param field
   *          the {@link IField} definition
   * @return a new {@link Collection}
   */
  Collection<?> createCollection(IField field);

  /**
   * Create a new instance of {@link Map}
   * 
   * @param field
   *          the field to create a map for
   * @return the new instance
   */
  Map<?, ?> createMap(IField field);

  /**
   * Set the Mapper, where the IObjectFactory is contained
   * 
   * @param mapper
   *          the {@link IMapper}
   */
  void setMapper(IMapper mapper);

  /**
   * Get the Mapper, where the IObjectFactory is contained
   * 
   * @return the {@link IMapper}
   */
  IMapper getMapper();
}
