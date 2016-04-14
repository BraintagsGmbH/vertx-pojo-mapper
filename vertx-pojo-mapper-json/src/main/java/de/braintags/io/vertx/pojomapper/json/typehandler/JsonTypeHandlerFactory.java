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

import java.lang.annotation.Annotation;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.BigDecimalTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.BigIntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CharSequenceTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CharacterTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ClassTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.EnumTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.FloatTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.GeoPointTypeHandlerJson;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.IdTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.IntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.LocaleTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.LongTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.PriceTypeHandler;
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
  private final ITypeHandler defaultHandler = new ObjectTypeHandler(this);
  private final ITypeHandler defaultHandleEmbedded = new ObjectTypeHandlerEmbedded(this);
  private final ITypeHandler defaultHandlerReferenced = new ObjectTypeHandlerReferenced(this);

  /**
   * Constructor calls the init method
   */
  public JsonTypeHandlerFactory() {
    init();
  }

  /**
   * Initializes the {@link ITypeHandler} which are belonging to the current instance
   */
  protected void init() {
    getDefinedTypeHandlers().add(0, new IdTypeHandler(this));

    add(new FloatTypeHandler(this));
    add(new DateTypeHandler(this));
    add(new ShortTypeHandler(this));
    add(new IntegerTypeHandler(this));
    add(new LongTypeHandler(this));
    add(new CalendarTypeHandler(this));
    add(new PriceTypeHandler(this));
    add(new BigDecimalTypeHandler(this));
    add(new BigIntegerTypeHandler(this));
    add(new CharSequenceTypeHandler(this));
    add(new CharacterTypeHandler(this));
    add(new ByteTypeHandler(this));
    add(new URITypeHandler(this));
    add(new URLTypeHandler(this));
    add(new GeoPointTypeHandlerJson(this));

    add(new CollectionTypeHandler(this));
    add(new CollectionTypeHandlerEmbedded(this));
    add(new CollectionTypeHandlerReferenced(this));
    add(new ClassTypeHandler(this));
    add(new LocaleTypeHandler(this));
    add(new EnumTypeHandler(this));
    add(new MapTypeHandler(this));
    add(new MapTypeHandlerEmbedded(this));
    add(new MapTypeHandlerReferenced(this));
    add(new StringTypeHandler(this));
    add(new ArrayTypeHandler(this));
    add(new ArrayTypeHandlerEmbedded(this));
    add(new ArrayTypeHandlerReferenced(this));
  }

  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    if (embedRef == null)
      return defaultHandler;
    return embedRef instanceof Embedded ? defaultHandleEmbedded : defaultHandlerReferenced;
  }

}
