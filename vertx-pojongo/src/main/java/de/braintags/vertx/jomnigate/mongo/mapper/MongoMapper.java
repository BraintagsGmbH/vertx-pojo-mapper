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

import de.braintags.vertx.jomnigate.json.mapping.jackson.JacksonMapper;
import de.braintags.vertx.jomnigate.json.mapping.jackson.MappedJacksonIdProperty;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mongo.mapper.datastore.MongoColumnInfo;

/**
 * An extension of {@link Mapper} for use with Mongo
 *
 * @author Michael Remme
 * 
 */

public class MongoMapper<T> extends JacksonMapper<T> {

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public MongoMapper(Class<T> mapperClass, MongoMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    checkIdField();
  }

  @Override
  protected IMappedIdField createIdProperty(IProperty property) {
    return new MappedJacksonIdProperty(property, MongoColumnInfo.ID_FIELD_NAME);
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
