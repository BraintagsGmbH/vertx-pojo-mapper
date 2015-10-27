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
package de.braintags.io.vertx.pojomapper.mysql.mapping;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.DefaultPropertyMapper;

/**
 * An implementation for Mysql
 * 
 * @author Michael Remme
 * 
 */

public class MySqlPropertyMapperFactory extends JsonPropertyMapperFactory {

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
      return new SqlEmbeddedMapper();
    } else if (cls.equals(IReferencedMapper.class) || IReferencedMapper.class.isInstance(cls)) {
      return new SqlReferencedMapper();
    } else if (cls.equals(IPropertyMapper.class) || IPropertyMapper.class.isInstance(cls)) {
      return new DefaultPropertyMapper();
    }
    throw new MappingException("could not create PropertyMapper for class: " + cls.getName());
  }

}
