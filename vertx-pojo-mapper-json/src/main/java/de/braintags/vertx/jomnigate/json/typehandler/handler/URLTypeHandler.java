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
package de.braintags.vertx.jomnigate.json.typehandler.handler;

import java.net.MalformedURLException;
import java.net.URL;

import de.braintags.vertx.jomnigate.mapping.IField;
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

public class URLTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public URLTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, URL.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    try {
      success(source == null ? source : new URL((String) source), resultHandler);
    } catch (MalformedURLException e) {
      fail(e, resultHandler);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : ((URL) source).toString(), resultHandler);
  }

}
