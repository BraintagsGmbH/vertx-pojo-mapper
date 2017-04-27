/*
 * #%L
 * jomnigate-sql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.sql.mapping;

import de.braintags.vertx.jomnigate.json.mapping.jackson.JacksonMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.sql.SqlDataStore;

/**
 * implementation of {@link IMapperFactory} for sql
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapperFactory extends JacksonMapperFactory {

  /**
   * @param dataStore
   */
  public SqlMapperFactory(SqlDataStore dataStore) {
    super(dataStore);
  }

  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new SqlMapper<>(mapperClass, this);
  }

}
