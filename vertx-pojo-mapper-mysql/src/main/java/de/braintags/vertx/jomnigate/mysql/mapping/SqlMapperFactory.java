/*-
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mysql.mapping;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapperFactory extends MapperFactory {

  /**
   * @param dataStore
   * @param typeHandlerFactory
   * @param propertyMapperFactory
   * @param stf
   */
  public SqlMapperFactory(IDataStore<Object, String> dataStore, ITypeHandlerFactory typeHandlerFactory,
      IPropertyMapperFactory propertyMapperFactory) {
    super(dataStore, typeHandlerFactory, propertyMapperFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.MapperFactory#createMapper(java.lang.Class)
   */
  @Override
  protected Mapper createMapper(Class mapperClass) {
    return new SqlMapper(mapperClass, this);
  }

}
