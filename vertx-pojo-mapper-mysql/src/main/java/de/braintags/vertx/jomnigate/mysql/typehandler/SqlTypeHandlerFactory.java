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

package de.braintags.vertx.jomnigate.mysql.typehandler;

import java.lang.annotation.Annotation;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.vertx.jomnigate.json.typehandler.handler.BigDecimalTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.BigIntegerTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CharSequenceTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CharacterTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ClassTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.EnumTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.FloatTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.IntegerTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.LocaleTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.LongTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.PriceTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ShortTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.StringTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.URITypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.URLTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * {@link ITypeHandlerFactory} for use with Sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlTypeHandlerFactory extends AbstractTypeHandlerFactory {
  private final ITypeHandler defaultHandler = new ObjectTypeHandler(this);
  private final ITypeHandler defaultHandleEmbedded = new SqlObjectTypehandlerEmbedded(this);
  private final ITypeHandler defaultHandlerReferenced = new SqlObjectTypehandlerReferenced(this);
  private JsonTypeHandlerFactory jsonFactory = new JsonTypeHandlerFactory();

  public SqlTypeHandlerFactory() {
    init();
  }

  protected void init() {
    getDefinedTypeHandlers().add(0, new SqlIdTypeHandler(this));
    getDefinedTypeHandlers().add(1, new BooleanTypeHandler(this));

    add(new FloatTypeHandler(this));
    add(new SqlDateTypeHandler(this));
    add(new ShortTypeHandler(this));
    add(new IntegerTypeHandler(this));
    add(new LongTypeHandler(this));
    add(new SqlCalendarTypehandler(this));
    add(new PriceTypeHandler(this));
    add(new BigDecimalTypeHandler(this));
    add(new BigIntegerTypeHandler(this));
    add(new CharSequenceTypeHandler(this));
    add(new CharacterTypeHandler(this));
    add(new SqlByteTypeHandler(this));
    add(new URITypeHandler(this));
    add(new URLTypeHandler(this));
    add(new SqlGeoPointTypeHandler(this));

    add(new SqlCollectionTypeHandler(this));
    add(new SqlCollectionTypeHandlerEmbedded(this));
    add(new SqlCollectionTypeHandlerReferenced(this));

    add(new ClassTypeHandler(this));
    add(new LocaleTypeHandler(this));
    add(new EnumTypeHandler(this));
    add(new SqlMapTypeHandler(this));
    add(new SqlMapTypeHandlerEmbedded(this));

    add(new SqlMapTypeHandlerReferenced(this));

    add(new StringTypeHandler(this));
    add(new SqlArrayTypehandler(this));
    add(new SqlArrayTypeHandlerEmbedded(this));
    add(new SqlArrayTypeHandlerReferenced(this));

    getDefinedTypeHandlers()
        .add(new de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.JsonTypeHandler(this));

  }

  /**
   * Some typehandlers, like {@link SqlCollectionTypeHandler}, are storing child objects of those Collections, which are
   * marked as {@link Embedded}, as JsonObjects. Thus, for the child processing, another {@link ITypeHandlerFactory} is
   * used
   * 
   * @return the {@link ITypeHandlerFactory} for child objects
   */
  public ITypeHandlerFactory getSubFactory() {
    return jsonFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.JsonTypeHandlerFactory#getDefaultTypeHandler(java.lang.annotation
   * .Annotation)
   */
  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    if (embedRef == null)
      return defaultHandler;
    return embedRef instanceof Embedded ? defaultHandleEmbedded : defaultHandlerReferenced;
  }

}
