/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.typehandler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * The ITypeHandlerFactory is used to create the convenient {@link ITypeHandler} for an implementation of
 * {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

public interface ITypeHandlerFactory {

  /**
   * Get the conventient {@link ITypeHandler} for the given field
   * 
   * @param field
   *          the field
   * @return a fitting {@link ITypeHandler}
   */
  ITypeHandler getTypeHandler(IField field);

  /**
   * Get the conventient {@link ITypeHandler} for the given Class
   * 
   * @param cls
   *          the class
   * @return a fitting {@link ITypeHandler}
   */
  ITypeHandler getTypeHandler(Class<?> cls);

}
