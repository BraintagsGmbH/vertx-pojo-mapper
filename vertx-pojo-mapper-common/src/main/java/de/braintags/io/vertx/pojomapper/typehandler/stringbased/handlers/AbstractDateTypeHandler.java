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
package de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.braintags.io.vertx.pojomapper.exception.DateParseException;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * An abstract implementation for Date and Time handling
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDateTypeHandler extends AbstractTypeHandler {
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd"; // yyyy.MM.dd
  private static final String TIME_FORMAT_PATTERN = "HH:mm:ss"; // HH:mm:ss
  private static final String DATETIME_FORMAT_PATTERN = DATE_FORMAT_PATTERN + " " + TIME_FORMAT_PATTERN + ".SSS"; // yyyy.MM.dd
                                                                                                                  // HH:mm:ss.ms
  private static DateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT_PATTERN);
  private static DateFormat timeFormater = new SimpleDateFormat(TIME_FORMAT_PATTERN);
  private static DateFormat dateTimeFormater = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);

  public static final Calendar DEFAULT_CALENDER = Calendar.getInstance();

  static {
    DEFAULT_CALENDER.setTimeInMillis(0);
  }

  /**
   * @param typeHandlerFactory
   * @param classesToDeal
   */
  public AbstractDateTypeHandler(ITypeHandlerFactory typeHandlerFactory, Class<?>... classesToDeal) {
    super(typeHandlerFactory, classesToDeal);
  }

  protected DateFormat getDateFormater() {
    return dateFormater;
  }

  protected DateFormat getTimeFormater() {
    return timeFormater;
  }

  protected DateFormat getDateTimeFormater() {
    return dateTimeFormater;
  }

  protected String formatDate(Calendar cal) {
    return dateFormater.format(cal.getTime());
  }

  protected Calendar parseDate(String dateString) {
    if (dateString == null || dateString.trim().hashCode() == 0) {
      return DEFAULT_CALENDER;
    } else {
      try {
        Date date = dateFormater.parse(dateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
      } catch (ParseException e) {
        throw new DateParseException(e);
      }
    }
  }

  protected String formatTime(Calendar cal) {
    return timeFormater.format(cal.getTime());
  }

  protected Calendar parseTime(String dateString) {
    if (dateString == null || dateString.trim().hashCode() == 0) {
      return DEFAULT_CALENDER;
    } else {
      try {
        Date date = timeFormater.parse(dateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
      } catch (ParseException e) {
        throw new DateParseException(e);
      }
    }
  }

  protected String formatDateTime(Calendar cal) {
    return dateTimeFormater.format(cal.getTime());
  }

  protected Calendar parseDateTime(String dateString) {
    if (dateString == null || dateString.trim().hashCode() == 0) {
      return DEFAULT_CALENDER;
    } else {
      try {
        Date date = dateTimeFormater.parse(dateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
      } catch (ParseException e) {
        throw new DateParseException(e);
      }
    }
  }

}
