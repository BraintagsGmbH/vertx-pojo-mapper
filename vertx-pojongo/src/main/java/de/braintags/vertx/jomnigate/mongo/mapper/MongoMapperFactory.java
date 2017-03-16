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

import de.braintags.vertx.jomnigate.json.mapping.jackson.JacksonMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;

/**
 * An extension of {@link MapperFactory}
 *
 * @author Michael Remme
 * 
 */

public class MongoMapperFactory extends JacksonMapperFactory {

  /**
   * @param dataStore
   */
  public MongoMapperFactory(MongoDataStore dataStore) {
    super(dataStore);
  }

  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new MongoMapper<>(mapperClass, this);
  }

}
