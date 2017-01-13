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
package de.braintags.io.vertx.pojomapper.mysql.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

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
  public SqlMapperFactory(IDataStore dataStore, ITypeHandlerFactory typeHandlerFactory,
      IPropertyMapperFactory propertyMapperFactory, IStoreObjectFactory stf) {
    super(dataStore, typeHandlerFactory, propertyMapperFactory, stf);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory#createMapper(java.lang.Class)
   */
  @Override
  protected Mapper createMapper(Class mapperClass) {
    return new SqlMapper(mapperClass, this);
  }

}
