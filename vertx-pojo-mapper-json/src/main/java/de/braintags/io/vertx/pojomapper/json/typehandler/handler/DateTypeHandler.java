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

package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;

/**
 * An {@link ITypeHandler} which is dealing {@link Date}. Currently its only dealing with the long value of a Date.
 * Could be modified to the use of ISO-8601 ( "$date", "1937-09-21T00:00:00+00:00" ) and the use of a date scanner (
 * eutil ). Question is: is it needed to store a Date / Time in a readable format in Mongo?
 * 
 * @author Michael Remme
 * 
 */

public class DateTypeHandler extends AbstractTypeHandler {

  /**
   * @param classesToDeal
   */
  public DateTypeHandler() {
    super(Date.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Object result = null;
    if (source != null) {
      Constructor<?> constr = field.getConstructor(long.class);
      if (constr == null) {
        fail(new MappingException("Contructor not found with long as parameter"), resultHandler);
        return;
      }
      try {
        result = constr.newInstance(source);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        fail(e, resultHandler);
        return;
      }
    }
    success(result, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : ((Date) source).getTime(), resultHandler);
  }

}
