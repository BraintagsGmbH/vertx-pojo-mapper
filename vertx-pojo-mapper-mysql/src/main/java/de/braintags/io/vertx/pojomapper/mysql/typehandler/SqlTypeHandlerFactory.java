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

import java.lang.annotation.Annotation;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.IdTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

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
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory#init()
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

    getDefinedTypeHandlers().add(new JsonTypeHandler(this));

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
   * de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory#getDefaultTypeHandler(java.lang.annotation
   * .Annotation)
   */
  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    if (embedRef == null)
      return defaultHandler;
    return embedRef instanceof Embedded ? defaultHandleEmbedded : defaultHandlerReferenced;
  }

}
