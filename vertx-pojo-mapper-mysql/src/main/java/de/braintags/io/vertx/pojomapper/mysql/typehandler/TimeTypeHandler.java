/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Implementation dealing with {@link Time}
 * 
 * @author Michael Remme
 * 
 */

public class TimeTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public TimeTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Time.class);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    try {
      Time time = source == null ? null
          : new Time(DateFormat.getTimeInstance(DateFormat.MEDIUM).parse((String) source).getTime());
      success(time, resultHandler);
    } catch (ParseException e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    String time = DateFormat.getTimeInstance(DateFormat.MEDIUM).format((Time) source);
    success(source == null ? source : time, resultHandler);
  }

}
