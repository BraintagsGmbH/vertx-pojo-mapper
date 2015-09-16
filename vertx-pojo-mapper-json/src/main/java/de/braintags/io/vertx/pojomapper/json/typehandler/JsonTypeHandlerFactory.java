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
package de.braintags.io.vertx.pojomapper.json.typehandler;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.BigDecimalTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.BigIntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CharSequenceTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CharacterTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ClassTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.EnumTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.FloatTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.LocaleTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectReferenceTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ShortTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.StringTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.URITypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.URLTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Creates {@link ITypeHandler} which are creating Json-usable formats from Objects and back
 * 
 * @author Michael Remme
 * 
 */

public class JsonTypeHandlerFactory extends AbstractTypeHandlerFactory {
  private static final ITypeHandler defaultHandler = new ObjectTypeHandler();
  private static final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  static {
    definedTypeHandlers.add(new FloatTypeHandler());
    definedTypeHandlers.add(new DateTypeHandler());
    definedTypeHandlers.add(new ShortTypeHandler());
    definedTypeHandlers.add(new CalendarTypeHandler());
    definedTypeHandlers.add(new BigDecimalTypeHandler());
    definedTypeHandlers.add(new BigIntegerTypeHandler());
    definedTypeHandlers.add(new CharSequenceTypeHandler());
    definedTypeHandlers.add(new CharacterTypeHandler());
    definedTypeHandlers.add(new ByteTypeHandler());
    definedTypeHandlers.add(new URITypeHandler());
    definedTypeHandlers.add(new URLTypeHandler());
    definedTypeHandlers.add(new CollectionTypeHandler());
    definedTypeHandlers.add(new ObjectReferenceTypeHandler());
    definedTypeHandlers.add(new ClassTypeHandler());
    definedTypeHandlers.add(new LocaleTypeHandler());
    definedTypeHandlers.add(new EnumTypeHandler());
    definedTypeHandlers.add(new MapTypeHandler());
    definedTypeHandlers.add(new StringTypeHandler());
    definedTypeHandlers.add(new ArrayTypeHandler());
  }

  /**
   * 
   */
  public JsonTypeHandlerFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory#getDefinedTypehandlers()
   */
  @Override
  public List<ITypeHandler> getDefinedTypehandlers() {
    return definedTypeHandlers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory#getDefaultTypeHandler()
   */
  @Override
  public ITypeHandler getDefaultTypeHandler() {
    return defaultHandler;
  }

}
