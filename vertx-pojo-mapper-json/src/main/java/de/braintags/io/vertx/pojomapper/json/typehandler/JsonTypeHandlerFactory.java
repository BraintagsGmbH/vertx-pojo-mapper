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
import java.util.ArrayList;
import java.util.List;

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
  private final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  /**
   * 
   */
  public JsonTypeHandlerFactory() {
    init();
  }

  /**
   * Initializes the {@link ITypeHandler} which are belonging to the current instance
   */
  protected void init() {
    getDefinedTypehandlers().add(0, new IdTypeHandler(this));

    definedTypeHandlers.add(new FloatTypeHandler(this));
    definedTypeHandlers.add(new DateTypeHandler(this));
    definedTypeHandlers.add(new ShortTypeHandler(this));
    definedTypeHandlers.add(new IntegerTypeHandler(this));
    definedTypeHandlers.add(new LongTypeHandler(this));
    definedTypeHandlers.add(new CalendarTypeHandler(this));
    definedTypeHandlers.add(new BigDecimalTypeHandler(this));
    definedTypeHandlers.add(new BigIntegerTypeHandler(this));
    definedTypeHandlers.add(new CharSequenceTypeHandler(this));
    definedTypeHandlers.add(new CharacterTypeHandler(this));
    definedTypeHandlers.add(new ByteTypeHandler(this));
    definedTypeHandlers.add(new URITypeHandler(this));
    definedTypeHandlers.add(new URLTypeHandler(this));
    definedTypeHandlers.add(new CollectionTypeHandler(this));
    definedTypeHandlers.add(new CollectionTypeHandlerEmbedded(this));
    definedTypeHandlers.add(new CollectionTypeHandlerReferenced(this));
    definedTypeHandlers.add(new ClassTypeHandler(this));
    definedTypeHandlers.add(new LocaleTypeHandler(this));
    definedTypeHandlers.add(new EnumTypeHandler(this));
    definedTypeHandlers.add(new MapTypeHandler(this));
    definedTypeHandlers.add(new MapTypeHandlerEmbedded(this));
    definedTypeHandlers.add(new MapTypeHandlerReferenced(this));
    definedTypeHandlers.add(new StringTypeHandler(this));
    definedTypeHandlers.add(new ArrayTypeHandler(this));
    definedTypeHandlers.add(new ArrayTypeHandlerEmbedded(this));
    definedTypeHandlers.add(new ArrayTypeHandlerReferenced(this));
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

  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    if (embedRef == null)
      return defaultHandler;
    return embedRef instanceof Embedded ? defaultHandleEmbedded : defaultHandlerReferenced;
  }

  /**
   * Remove the typehandler specified by the given class
   * 
   * @param typeHandlerClass
   *          the class of the typehandler, which shall be removed
   */
  public void remove(Class typeHandlerClass) {
    for (int i = definedTypeHandlers.size() - 1; i >= 0; i--) {
      if (definedTypeHandlers.get(i).getClass() == typeHandlerClass)
        definedTypeHandlers.remove(i);
    }
  }

}
