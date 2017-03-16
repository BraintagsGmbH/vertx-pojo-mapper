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
package de.braintags.vertx.jomnigate.typehandler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IProperty;

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
  private final Map<TypeHandlerCacheKey, ITypeHandler> cachedTypeHandler = new HashMap<TypeHandlerCacheKey, ITypeHandler>();
  private final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  /**
   * 
   */
  public AbstractTypeHandlerFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory#getTypeHandler(de.braintags.vertx.jomnigate
   * .mapping.IField)
   */
  @Override
  public ITypeHandler getTypeHandler(IProperty field) {
    // here we should NOT use a cache, otherwise the method examineMatch of the TypeHandler isn't called,
    // which is important, cause this method can decide on other parameters than the class
    ITypeHandler handler = examineMatch(field);
    if (handler == null)
      handler = getDefaultTypeHandler(field.getEmbedRef());
    return (ITypeHandler) handler.clone();
  }

  @Override
  public ITypeHandler getTypeHandler(Class<?> fieldClass, Annotation annotation) {
    TypeHandlerCacheKey key = new TypeHandlerCacheKey(fieldClass, annotation);
    if (cachedTypeHandler.containsKey(key))
      return cachedTypeHandler.get(key);
    ITypeHandler handler = examineMatch(fieldClass, annotation);
    if (handler == null)
      handler = getDefaultTypeHandler(annotation);
    cachedTypeHandler.put(key, handler);
    return (ITypeHandler) handler.clone();
  }

  /**
   * Checks for a valid TypeHandler by respecting graded results
   * 
   * @param field
   * @param annotation
   *          an annotation of type {@link Referenced} or {@link Embedded} or NULL. ITypeHandler can react to this
   *          information
   * @return
   */
  private ITypeHandler examineMatch(Class<?> cls, Annotation annotation) {
    ITypeHandler returnHandler = null;
    for (ITypeHandler th : getDefinedTypeHandlers()) {
      short matchResult = th.matches(cls, annotation);
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
  private ITypeHandler examineMatch(IProperty field) {
    ITypeHandler returnHandler = null;
    List<ITypeHandler> ths = getDefinedTypeHandlers();
    for (ITypeHandler th : ths) {
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
  public List<ITypeHandler> getDefinedTypeHandlers() {
    return definedTypeHandlers;
  }

  /**
   * Remove the typehandler specified by the given class
   * 
   * @param typeHandlerClass
   *          the class of the typehandler, which shall be removed
   */
  public void remove(Class<? extends ITypeHandler> typeHandlerClass) {
    for (int i = definedTypeHandlers.size() - 1; i >= 0; i--) {
      if (definedTypeHandlers.get(i).getClass() == typeHandlerClass)
        definedTypeHandlers.remove(i);
    }
  }

  /**
   * Add a new typehandler
   * 
   * @param th
   */
  protected final void add(ITypeHandler th) {
    getDefinedTypeHandlers().add(th);
  }

  /**
   * Remove all entries of the class definition and add the new one
   * 
   * @param typeHandlerClass
   * @param th
   */
  public void replace(Class<? extends ITypeHandler> typeHandlerClass, ITypeHandler th) {
    remove(typeHandlerClass);
    add(th);
  }

  /**
   * Get the default {@link ITypeHandler}
   * 
   * @param embedRef
   *          an annotation instance of {@link Embedded}, {@link Referenced} or null
   * 
   * @return the default {@link ITypeHandler}
   */
  public abstract ITypeHandler getDefaultTypeHandler(Annotation embedRef);

  private class TypeHandlerCacheKey {
    private String cacheKey;

    TypeHandlerCacheKey(Class cls, Annotation annotation) {
      String annString = annotation == null ? "" : annotation.toString();
      cacheKey = cls.getName() + annString;
    }

    @Override
    public String toString() {
      return cacheKey;
    }
  }

}
