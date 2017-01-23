/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.mapper;

import java.lang.reflect.Field;

import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.impl.MappedField;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;

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
   * @see de.braintags.vertx.jomnigate.mapping.impl.Mapper#createMappedField(java.lang.reflect.Field,
   * de.braintags.vertx.jomnigate.mapping.IPropertyAccessor)
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
