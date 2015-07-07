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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;

/**
 * default implementation of {@link IPropertyMapperFactory}
 * 
 * @author Michael Remme
 * 
 */

public class DefaultPropertyMapperFactory implements IPropertyMapperFactory {

  /**
   * 
   */
  public DefaultPropertyMapperFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory#getPropertyMapper(java.lang.Class)
   */
  @Override
  public IPropertyMapper getPropertyMapper(Class<? extends IPropertyMapper> cls) {
    if (cls == null)
      throw new NullPointerException("parameter must be specified: cls");
    if (cls.equals(IEmbeddedMapper.class) || IEmbeddedMapper.class.isInstance(cls)) {
      return new DefaultEmbeddedMapper();
    } else if (cls.equals(IReferencedMapper.class) || IReferencedMapper.class.isInstance(cls)) {
      return new DefaultReferencedMapper();
    } else if (cls.equals(IPropertyMapper.class) || IPropertyMapper.class.isInstance(cls)) {
      return new DefaultPropertyMapper();
    }
    throw new MappingException("could not create PropertyMapper for class: " + cls.getName());
  }

}
