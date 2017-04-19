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
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;

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
  public MongoMapper(final Class<T> mapperClass, final MongoMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    checkIdField();
  }

  /**
   * Currently the id field for mongo must be character
   */
  @SuppressWarnings("rawtypes")
  private void checkIdField() {
    Class idClass = getIdInfo().getField().getType();
    if (!CharSequence.class.isAssignableFrom(idClass))
      throw new UnsupportedOperationException(
          "Currently the id field must be Character based for mongo driver. Class: " + getMapperClass());
  }

}
