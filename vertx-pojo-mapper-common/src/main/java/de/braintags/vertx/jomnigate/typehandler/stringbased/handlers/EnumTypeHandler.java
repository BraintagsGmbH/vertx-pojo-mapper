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
package de.braintags.vertx.jomnigate.typehandler.stringbased.handlers;

import java.util.List;

import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class EnumTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public EnumTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Enum.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    try {
      Class enumClass = field == null ? cls : getEnumClass(field);
      if (enumClass == null) {
        throw new IllegalArgumentException("could not get enum class");
      }
      success(source == null || source.toString().trim().hashCode() == 0 ? null
          : Enum.valueOf(enumClass, source.toString()), resultHandler);
    } catch (Exception e) {
      fail(e, resultHandler);
    }
  }

  private Class getEnumClass(IProperty field) {
    Class enClass = field.getType();
    if (enClass.isEnum()) {
      return enClass;
    }

    List<IProperty> tp = field.getTypeParameters();
    if (!tp.isEmpty()) {
      return tp.get(0).getType();
    }
    throw new UnsupportedOperationException("Enum without generic are not supported in entity " + field.getFullName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : getName((Enum) source), resultHandler);
  }

  private <T extends Enum> String getName(final T value) {
    return value.name();
  }
}
