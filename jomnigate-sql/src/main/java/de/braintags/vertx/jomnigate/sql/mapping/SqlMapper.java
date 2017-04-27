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

import de.braintags.vertx.jomnigate.json.mapping.jackson.JacksonMapper;

/**
 * implementation of IMapper for sql based datastores
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapper<T> extends JacksonMapper<T> {

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public SqlMapper(Class<T> mapperClass, SqlMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
  }

}
