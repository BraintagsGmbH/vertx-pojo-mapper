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
import java.lang.reflect.Type;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.impl.MappedField;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoMappedField extends MappedField {
  private static final String ID_FIELD_NAME = "_id";

  /**
   * @param field
   * @param accessor
   * @param mapper
   */
  public MongoMappedField(Field field, IPropertyAccessor accessor, Mapper mapper) {
    super(field, accessor, mapper);
  }

  /**
   * @param type
   * @param mapper
   */
  public MongoMappedField(Type type, Mapper mapper) {
    super(type, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.MappedField#computePropertyName()
   */
  @Override
  protected String computePropertyName() {
    if (hasAnnotation(Id.class)) {
      return ID_FIELD_NAME;
    }
    return super.computePropertyName();
  }

}
