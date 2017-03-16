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
import de.braintags.vertx.jomnigate.mapping.MappedIdField;
import de.braintags.vertx.jomnigate.mapping.impl.MappedField;
import de.braintags.vertx.jomnigate.mapping.impl.MappedIdFieldImpl;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.jomnigate.mongo.mapper.datastore.MongoColumnInfo;

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.Mapper#createIdField(de.braintags.vertx.jomnigate.mapping.impl.
   * MappedField)
   */
  @Override
  protected MappedIdField createIdField(MappedField mappedField) {
    return new MappedIdFieldImpl(mappedField, MongoColumnInfo.ID_FIELD_NAME);
  }

  /**
   * Currently the id field for mongo must be character
   */
  @SuppressWarnings("rawtypes")
  private void checkIdField() {
    Class idClass = getIdField().getField().getType();
    if (!CharSequence.class.isAssignableFrom(idClass))
      throw new UnsupportedOperationException(
          "Currently the id field must be Character based for mongo driver. Class: " + getMapperClass());
  }

}
