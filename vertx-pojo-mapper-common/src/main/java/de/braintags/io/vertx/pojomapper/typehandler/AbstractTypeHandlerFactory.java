/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.typehandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;

/**
 * An abstract implementation of {@link ITypeHandlerFactory}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractTypeHandlerFactory implements ITypeHandlerFactory {
  /**
   * If for a class a {@link ITypeHandler} was requested and found, it is cached by here with the class to handle as key
   */
  private final Map<Class<?>, ITypeHandler> cachedTypeHandler = new HashMap<Class<?>, ITypeHandler>();

  /**
   * 
   */
  public AbstractTypeHandlerFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory#getTypeHandler(de.braintags.io.vertx.pojomapper
   * .mapping.IField)
   */
  @Override
  public ITypeHandler getTypeHandler(IField field) {
    if (field.getPropertyMapper() instanceof IReferencedMapper || field.getPropertyMapper() instanceof IEmbeddedMapper)
      return null;
    Class<?> fieldClass = field.getType();
    if (cachedTypeHandler.containsKey(fieldClass))
      return cachedTypeHandler.get(fieldClass);
    ITypeHandler handler = examineExcactMatch(fieldClass);
    if (handler == null)
      handler = examineInstanceOfMatch(fieldClass);
    if (handler == null)
      handler = getDefaultTypeHandler();
    cachedTypeHandler.put(fieldClass, handler);
    return handler;
  }

  private ITypeHandler examineExcactMatch(Class<?> cls) {
    for (ITypeHandler th : getDefinedTypehandlers()) {
      if (th.matchesExcact(cls))
        return th;
    }
    return null;
  }

  private ITypeHandler examineInstanceOfMatch(Class<?> cls) {
    for (ITypeHandler th : getDefinedTypehandlers()) {
      if (th.matchesInstance(cls))
        return th;
    }
    return null;
  }

  /**
   * Get a list of all defined {@link ITypeHandler} which shall be used by the current instance
   * 
   * @return list of {@link ITypeHandler}
   */
  public abstract List<ITypeHandler> getDefinedTypehandlers();

  /**
   * Get the default {@link ITypeHandler}
   * 
   * @return the default {@link ITypeHandler}
   */
  public abstract ITypeHandler getDefaultTypeHandler();
}
