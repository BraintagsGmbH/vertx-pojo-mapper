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
package de.braintags.vertx.jomnigate.json.mapping;

import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mapping.impl.DefaultPropertyMapper;

/**
 * implementation of {@link IPropertyMapperFactory} for Json based datastores. The implementation uses jackson to
 * serialize and deserialize mapper instances
 * 
 * @author Michael Remme
 * 
 */

public class JsonPropertyMapperFactory implements IPropertyMapperFactory {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory#getPropertyMapper(java.lang.Class)
   */
  @Override
  public IPropertyMapper getPropertyMapper(IProperty field) {
    if (field == null)
      throw new NullPointerException("parameter must be specified: field");
    if (field.isIdField()) {
      return new IdPropertyMapper(field);
    }
    return new DefaultPropertyMapper();
  }

}
