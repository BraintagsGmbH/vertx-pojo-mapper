/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapping;

/**
 * Factory to build instances of {@link IPropertyMapper}
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyMapperFactory {

  /**
   * Get an instance of {@link IPropertyMapper} for the given {@link IProperty}
   * 
   * @param field
   *          the field to request a property mapper for
   * @return the generated {@link IPropertyMapper} for the given field
   */
  public IPropertyMapper getPropertyMapper(IProperty field);
}
