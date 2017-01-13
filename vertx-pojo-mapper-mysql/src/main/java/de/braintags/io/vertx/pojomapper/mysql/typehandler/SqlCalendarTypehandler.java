/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import java.util.Calendar;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * This implementation deals with {@link Calendar} datatypes
 * 
 * @author Michael Remme
 * 
 */

public class SqlCalendarTypehandler extends CalendarTypeHandler {
  private static final DateTimeFormatter formater = ISODateTimeFormat.dateHourMinuteSecondMillis();

  /**
   * Constructor
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public SqlCalendarTypehandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source != null) {
      long millis = formater.parseMillis((String) source);
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(millis);
      success(cal, resultHandler);
    } else
      success(null, resultHandler);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Object value = source == null ? source : formater.print(((Calendar) source).getTimeInMillis());
    success(value, resultHandler);
  }

}
