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
package de.braintags.io.vertx.pojomapper.mongo.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

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

}
