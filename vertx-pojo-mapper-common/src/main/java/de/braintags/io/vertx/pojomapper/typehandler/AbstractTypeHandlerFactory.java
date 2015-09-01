/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
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
    ITypeHandler handler = examineMatch(field);
    if (handler == null)
      handler = getDefaultTypeHandler();
    cachedTypeHandler.put(fieldClass, handler);
    return handler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory#getTypeHandler(java.lang.Class)
   */
  @Override
  public ITypeHandler getTypeHandler(Class<?> fieldClass) {
    if (cachedTypeHandler.containsKey(fieldClass))
      return cachedTypeHandler.get(fieldClass);
    ITypeHandler handler = examineMatch(fieldClass);
    if (handler == null)
      handler = getDefaultTypeHandler();
    cachedTypeHandler.put(fieldClass, handler);
    return handler;
  }

  /**
   * Checks for a valid TypeHandler by respecting graded results
   * 
   * @param field
   * @return
   */
  private ITypeHandler examineMatch(Class<?> cls) {
    ITypeHandler returnHandler = null;
    for (ITypeHandler th : getDefinedTypehandlers()) {
      short matchResult = th.matches(cls);
      switch (matchResult) {
      case ITypeHandler.MATCH_MAJOR:
        return th;

      case ITypeHandler.MATCH_MINOR:
        returnHandler = th;
        break;

      default:
        break;
      }
    }
    return returnHandler;
  }

  /**
   * Checks for a valid TypeHandler by respecting graded results
   * 
   * @param field
   * @return
   */
  @SuppressWarnings("unused")
  private ITypeHandler examineMatch(IField field) {
    ITypeHandler returnHandler = null;
    List<ITypeHandler> ths = getDefinedTypehandlers();
    for (ITypeHandler th : ths) {
      if (th.getClass().getName().equals("de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler")) {
        String test = "test";
      }
      short matchResult = th.matches(field);
      switch (matchResult) {
      case ITypeHandler.MATCH_MAJOR:
        return th;

      case ITypeHandler.MATCH_MINOR:
        returnHandler = th;
        break;

      default:
        break;
      }
    }
    return returnHandler;
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
