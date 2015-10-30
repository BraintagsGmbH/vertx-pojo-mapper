/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Deals all fields, which are instanced of Object and which are NOT annotated as {@link Referenced} or {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class ObjectTypeHandler extends AbstractTypeHandler {
  private static final Class<?>[] handleClass = { Object.class };

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ObjectTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, handleClass);
  }

  @Override
  public final short matches(IField field) {
    if (matchAnnotation(field) == MATCH_NONE)
      return MATCH_NONE;
    return super.matches(field);
  }

  /**
   * Checks, wether an annotation like {@link Referenced} or {@link Embedded} is set to the field and returns the
   * propriate match definition. If the method returns MATCH_NONE, then the class won't be checkd, otherwise it will be
   * checked
   * 
   * @param field
   *          the field to be checked
   * @return MATCH_NONE or MATCH_MINOR
   */
  protected short matchAnnotation(IField field) {
    if (field.hasAnnotation(Referenced.class) || field.hasAnnotation(Embedded.class))
      return MATCH_NONE;
    return MATCH_MINOR;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

}
