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
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ByteTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.DateTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.GeoPointTypeHandlerJson;
import de.braintags.vertx.jomnigate.json.typehandler.handler.IdTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * {@link ITypeHandlerFactory} for use with Sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlTypeHandlerFactory extends JsonTypeHandlerFactory {
  private final ITypeHandler defaultHandler = new ObjectTypeHandler(this);
  private final ITypeHandler defaultHandleEmbedded = new SqlObjectTypehandlerEmbedded(this);
  private final ITypeHandler defaultHandlerReferenced = new SqlObjectTypehandlerReferenced(this);
  private JsonTypeHandlerFactory jsonFactory = new JsonTypeHandlerFactory();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.JsonTypeHandlerFactory#init()
   */
  @Override
  protected void init() {
    super.init();
    remove(IdTypeHandler.class);
    getDefinedTypeHandlers().add(0, new SqlIdTypeHandler(this));
    getDefinedTypeHandlers().add(1, new BooleanTypeHandler(this));

    remove(DateTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlDateTypeHandler(this));

    remove(CalendarTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlCalendarTypehandler(this));

    remove(ByteTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlByteTypeHandler(this));

    getDefinedTypeHandlers()
        .add(new de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.JsonTypeHandler(this));

    remove(GeoPointTypeHandlerJson.class);
    getDefinedTypeHandlers().add(new SqlGeoPointTypeHandler(this));

    remove(ArrayTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlArrayTypehandler(this));
    remove(ArrayTypeHandlerEmbedded.class);
    getDefinedTypeHandlers().add(new SqlArrayTypeHandlerEmbedded(this));
    remove(ArrayTypeHandlerReferenced.class);
    getDefinedTypeHandlers().add(new SqlArrayTypeHandlerReferenced(this));

    remove(MapTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlMapTypeHandler(this));
    remove(MapTypeHandlerEmbedded.class);
    getDefinedTypeHandlers().add(new SqlMapTypeHandlerEmbedded(this));
    remove(MapTypeHandlerReferenced.class);
    getDefinedTypeHandlers().add(new SqlMapTypeHandlerReferenced(this));

    remove(CollectionTypeHandler.class);
    getDefinedTypeHandlers().add(new SqlCollectionTypeHandler(this));
    remove(CollectionTypeHandlerEmbedded.class);
    getDefinedTypeHandlers().add(new SqlCollectionTypeHandlerEmbedded(this));
    remove(CollectionTypeHandlerReferenced.class);
    getDefinedTypeHandlers().add(new SqlCollectionTypeHandlerReferenced(this));

  }

  /**
   * Some typehandlers, like {@link SqlCollectionTypeHandler}, are storing child objects of those Collections, which are
   * marked as {@link Embedded}, as JsonObjects. Thius, for the child processing, another {@link ITypeHandlerFactory} is
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
