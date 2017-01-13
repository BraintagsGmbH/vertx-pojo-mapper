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
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.pojomapper.typehandler.impl.DefaultTypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * The IdTypeHandler is a special {@link ITypeHandler} which is used specially for the id field. Contrary to normal
 * fields, where the field type and column type should be "type safe", the ID field can vary, especially when a mapper
 * is moved from Mongo to MySql, for instance. In this case the java field can be String but the column can be numeric,
 * or vice versa.
 * 
 * NOTE: this is still a hack, cause we are expecting the id field to be a String or numeric field, because of a
 * potential switch from Mongo to MySql. Better implementation could be a TypeHandler, which chains two Typehandlers
 * which are reacting to the type of the java field on the one hand and on the type of the column on the other hand!
 * 
 * @author Michael Remme
 * 
 */

public class IdTypeHandler extends AbstractTypeHandler {
  private ITypeHandler internalTypehandler;

  /**
   * @param typeHandlerFactory
   *          the {@link ITypeHandlerFactory} where the current instance is part of
   */
  public IdTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public final void fromStore(final Object id, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    @SuppressWarnings("rawtypes")
    Class fieldClass = field.getType();
    Object internalId = id;
    try {
      if (fieldClass.equals(Long.class) || fieldClass.equals(long.class)) {
        internalId = convertToLong(id);
      } else if (fieldClass.equals(Integer.class) || fieldClass.equals(int.class)) {
        internalId = convertToInt(id);
      } else if (fieldClass.equals(String.class)) {
        internalId = convertToString(id);
      } else
        throw new UnsupportedOperationException("unsupported type for id field: " + fieldClass.getName());
      getInternalTypeHandler(field).fromStore(internalId, field, cls, resultHandler);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  private final Object convertToString(Object id) {
    if (id == null) {
      return id;
    }
    if (id instanceof Number) {
      return String.valueOf(id);
    }
    if (id instanceof String) {
      return id;
    }
    return String.valueOf(id);
  }

  private final Object convertToLong(Object id) {
    if (id == null) {
      return id;
    }
    if (id instanceof String) {
      return Long.parseLong((String) id);
    }
    if (id instanceof Number) {
      return ((Number) id).longValue() == 0 ? null : id;
    }
    throw new UnsupportedOperationException("unsupported type to convert: " + id.getClass().getName());
  }

  private final Object convertToInt(Object id) {
    if (id == null) {
      return id;
    }
    if (id instanceof String) {
      return Integer.parseInt((String) id);
    }
    if (id instanceof Number) {
      return id;
    }
    throw new UnsupportedOperationException("unsupported type to convert: " + id.getClass().getName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public final void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    // getInternalTypeHandler(field).intoStore(source, field, resultHandler);
    // here we would need an ITypehandler reacting to the column type in spite of the java field, if types are different
    if (isCharacterColumn(field)) {
      source = convertToString(source);
    } else if (isNumericColumn(field)) {
      source = convertToLong(source);
    } else
      resultHandler.handle(Future.failedFuture(
          new UnsupportedOperationException("id column is nor numeric nor character: " + field.getType())));

    DefaultTypeHandlerResult thResult = new DefaultTypeHandlerResult(source);
    resultHandler.handle(Future.succeededFuture(thResult));
  }

  protected boolean isCharacterColumn(IField field) {
    return CharSequence.class.isAssignableFrom(field.getType());
  }

  protected boolean isNumericColumn(IField field) {
    return Number.class.isAssignableFrom(field.getType()) || field.getType().equals(long.class)
        || field.getType().equals(int.class);
  }

  private final ITypeHandler getInternalTypeHandler(IField field) {
    if (internalTypehandler == null) {
      internalTypehandler = getTypeHandlerFactory().getTypeHandler(field.getType(), field.getEmbedRef());
    }
    return internalTypehandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matches(de.braintags.io.vertx.pojomapper.mapping.
   * IField)
   */
  @Override
  public final short matches(IField field) {
    if (field.getMapper().getIdField() == field)
      return MATCH_MAJOR;
    return MATCH_NONE;
  }

}
