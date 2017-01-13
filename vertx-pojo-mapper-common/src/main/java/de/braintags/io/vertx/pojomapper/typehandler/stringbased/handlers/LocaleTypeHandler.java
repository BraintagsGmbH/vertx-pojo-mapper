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

import java.util.Locale;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class LocaleTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public LocaleTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Locale.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : parseLocale((String) source), resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : source.toString(), resultHandler);
  }

  public static Locale parseLocale(final String localeString) {
    if ((localeString != null) && (localeString.length() != 0)) {
      final int index = localeString.indexOf('_');
      final int index2 = localeString.indexOf('_', index + 1);
      Locale resultLocale;
      if (index == -1) {
        resultLocale = new Locale(localeString);
      } else if (index2 == -1) {
        resultLocale = new Locale(localeString.substring(0, index), localeString.substring(index + 1));
      } else {
        resultLocale = new Locale(localeString.substring(0, index), localeString.substring(index + 1, index2),
            localeString.substring(index2 + 1));

      }
      return resultLocale;
    }

    return null;
  }
}
