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

import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * {@link ITypeHandlerFactory} for use with Sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlTypeHandlerFactory extends JsonTypeHandlerFactory {

  /**
   * 
   */
  public SqlTypeHandlerFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory#init()
   */
  @Override
  protected void init() {
    super.init();
    getDefinedTypehandlers().add(0, new IdTypeHandler(this));
    getDefinedTypehandlers().add(0, new BooleanTypeHandler(this));

    remove(DateTypeHandler.class);
    getDefinedTypehandlers().add(new SqlDateTypeHandler(this));

    remove(CalendarTypeHandler.class);
    getDefinedTypehandlers().add(new SqlCalendarTypehandler(this));

    remove(ByteTypeHandler.class);
    getDefinedTypehandlers().add(new SqlByteTypeHandler(this));

    getDefinedTypehandlers().add(new JsonTypeHandler(this));
  }

}
