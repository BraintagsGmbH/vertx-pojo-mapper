/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.mapping.jackson;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractMapperFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * An implementation of {@link IMapperFactory} which uses jacksons ObjectMapper to perform the mapping of pojos
 * 
 * @author Michael Remme
 * 
 */
public class JacksonMapperFactory extends AbstractMapperFactory {

  /**
   * @param dataStore
   */
  public JacksonMapperFactory(IDataStore<?, ?> dataStore) {
    super(dataStore);
  }

  /**
   * This method is called if mapping was not processed yet and should create a new mapper instance
   * 
   * @param mapperClass
   * @return
   */
  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new JacksonMapper<>(mapperClass, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getTypeHandlerFactory()
   */
  @Override
  public ITypeHandlerFactory getTypeHandlerFactory() {
    throw new UnsupportedOperationException("JacksonMapperFactory is not allowed to use typehandlers");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getPropertyMapperFactory()
   */
  @Override
  public IPropertyMapperFactory getPropertyMapperFactory() {
    throw new UnsupportedOperationException("JacksonMapperFactory is not allowed to use IPropertyMapper");
  }

}
