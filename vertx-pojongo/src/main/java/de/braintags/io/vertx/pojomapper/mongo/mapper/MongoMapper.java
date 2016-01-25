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
package de.braintags.io.vertx.pojomapper.mongo.mapper;

import java.lang.reflect.Field;

import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.impl.MappedField;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;

/**
 * An extension of {@link Mapper} for use with Mongo
 *
 * @author Michael Remme
 * 
 */

public class MongoMapper extends Mapper {

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public MongoMapper(Class<?> mapperClass, MapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    checkIdField();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.Mapper#createMappedField(java.lang.reflect.Field,
   * de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor)
   */
  @Override
  protected MappedField createMappedField(Field field, IPropertyAccessor accessor) {
    return new MongoMappedField(field, accessor, this);
  }

  /**
   * Currently the id field for mongo must be character
   */
  @SuppressWarnings("rawtypes")
  private void checkIdField() {
    Class idClass = getIdField().getType();
    if (!CharSequence.class.isAssignableFrom(idClass))
      throw new UnsupportedOperationException(
          "Currently the id field must be Character based for mongo driver. Class: " + getMapperClass());
  }

}
