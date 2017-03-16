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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * An extension of {@link MapperFactory}
 *
 * @author Michael Remme
 * 
 */

public class MongoMapperFactory extends MapperFactory {

  /**
   * @param dataStore
   */
  public MongoMapperFactory(IDataStore dataStore, ITypeHandlerFactory typehandlerFactory,
      IPropertyMapperFactory propertyMapperFactory) {
    super(dataStore, typehandlerFactory, propertyMapperFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.MapperFactory#createMapper(java.lang.Class)
   */
  @Override
  protected Mapper createMapper(Class mapperClass) {
    return new MongoMapper(mapperClass, this);
  }

}
