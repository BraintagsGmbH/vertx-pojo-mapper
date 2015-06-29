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

/**
 * IMapperFactory is responsible to create and store instances of {@link IMapper} for all classes, which shall be
 * persisted into the datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IMapperFactory {

  /**
   * Retrieve the {@link IMapper} for the given class
   * 
   * @param mapperClass
   * @return
   */
  IMapper getMapper(Class<?> mapperClass);

}
