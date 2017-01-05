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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.impl.DefaultTypeHandlerResult;
import de.braintags.io.vertx.util.ClassUtil;
import de.braintags.io.vertx.util.ExceptionUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link ITypeHandler} which handles
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractTypeHandler implements ITypeHandler {
  private List<Class<?>> classesToHandle;
  private ITypeHandlerFactory typeHandlerFactory;

  /**
   * Constructor for an implementation
   * 
   * @param typeHandlerFactory
   *          the parent factory
   * @param classesToDeal
   *          the classes to deal with
   */
  public AbstractTypeHandler(ITypeHandlerFactory typeHandlerFactory, Class<?>... classesToDeal) {
    this.typeHandlerFactory = typeHandlerFactory;
    classesToHandle = Arrays.asList(classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#matches(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public short matches(IField field) {
    return matches(field.getType(), field.getEmbedRef());
  }

  @Override
  public final short matches(Class<?> cls, Annotation annotation) {
    if (matchesAnnotation(annotation)) {
      return matchesClass(cls);
    }
    return MATCH_NONE;
  }

  /**
   * Checks wether the given Class is handled by the current implementation
   * 
   * @param cls
   *          the class to be checked
   * @return MATCH_NONE if the field is not handled by the current instance, MATCH_MINOR if it is fitting partially (
   *         like a subclass for instance ) or MATCH_MAJOR if it is highly fitting, like the exact class for instance
   */
  protected short matchesClass(Class<?> cls) {
    for (Class<?> dCls : classesToHandle) {
      if (dCls.equals(cls))
        return MATCH_MAJOR;
    }
    for (Class<?> dCls : classesToHandle) {
      if (dCls.isAssignableFrom(cls))
        return MATCH_MINOR;
    }
    return MATCH_NONE;
  }

  /**
   * Checks wether the given implementation handles the Annotation of type Referenced or Embedded or null
   * 
   * @param annotation
   *          an Annotation of type {@link Referenced}, {@link Embedded} or null
   * @return true, if the current implementation handles this. The default implementation returns true
   */
  protected boolean matchesAnnotation(Annotation annotation) {
    if (annotation != null && !(annotation instanceof Referenced) && !(annotation instanceof Embedded))
      throw new MappingException("The annotation in this context must be Referenced, Embedded or null");
    return true;
  }

  /**
   * Get a fitting constructor
   * 
   * @param field
   *          the field to be used
   * @param cls
   *          if field is null, then the class will be used as source
   * @param arguments
   *          the arguments for the constructor
   * @return a fitting {@link Constructor}
   */
  @SuppressWarnings("rawtypes")
  public Constructor getConstructor(IField field, Class<?> cls, Class<?>... arguments) {
    if (field == null && cls == null)
      throw new NullPointerException("Class and field is null");
    if (field != null) {
      Constructor<?> constr = field.getConstructor(arguments);
      if (constr == null)
        throw new MappingException(
            "Constructor not found with arguments as parameter for field " + field.getFullName());
      return constr;
    }
    return ClassUtil.getConstructor(cls, arguments);
  }

  /**
   * Creates an instance of {@link ITypeHandlerResult} and calls the handler
   * 
   * @param result
   *          the result to be sent to the caller
   * @param resultHandler
   *          the caller
   */
  protected void success(Object result, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    DefaultTypeHandlerResult thResult = new DefaultTypeHandlerResult(result);
    Future<ITypeHandlerResult> future = Future.succeededFuture(thResult);
    resultHandler.handle(future);
  }

  /**
   * Calls the caller to inform about an error
   * 
   * @param thr
   *          the Throwable, which occured
   * @param resultHandler
   *          the caller
   */
  protected void fail(Throwable thr, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Future<ITypeHandlerResult> future = Future.failedFuture(thr);
    resultHandler.handle(future);
  }

  @Override
  public final ITypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#getSubTypeHandler(java.lang.Class)
   */
  @Override
  public ITypeHandler getSubTypeHandler(Class<?> subClass, Annotation embedRef) {
    return getTypeHandlerFactory().getTypeHandler(subClass, embedRef);
  }

}
