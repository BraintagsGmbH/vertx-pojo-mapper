/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.typehandler.stringbased;

import java.lang.annotation.Annotation;

import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BigDecimalTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BigIntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BooleanTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CharSequenceTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CharacterTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ClassTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.DoubleTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.EnumTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.FloatTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.IntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.JsonTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.LocaleTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.LongTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ShortTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.TimeTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.TimestampTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.URITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.URLTypeHandler;

/**
 * Creates {@link ITypeHandler} which are creating String from Objects and back
 * 
 * @author Michael Remme
 * 
 */

public class StringTypeHandlerFactory extends AbstractTypeHandlerFactory {
  private final ITypeHandler defaultHandler = new ObjectTypeHandler(this);

  /**
   * The default constructor for a String base factory
   */
  public StringTypeHandlerFactory() {
    init();
  }

  protected void init() {
    getDefinedTypeHandlers().add(new BooleanTypeHandler(this));
    getDefinedTypeHandlers().add(new JsonTypeHandler(this));
    getDefinedTypeHandlers().add(new CharacterTypeHandler(this));
    getDefinedTypeHandlers().add(new BigDecimalTypeHandler(this));
    getDefinedTypeHandlers().add(new BigIntegerTypeHandler(this));
    getDefinedTypeHandlers().add(new FloatTypeHandler(this));
    getDefinedTypeHandlers().add(new DoubleTypeHandler(this));
    getDefinedTypeHandlers().add(new ShortTypeHandler(this));
    getDefinedTypeHandlers().add(new IntegerTypeHandler(this));
    getDefinedTypeHandlers().add(new LongTypeHandler(this));
    getDefinedTypeHandlers().add(new TimestampTypeHandler(this));
    getDefinedTypeHandlers().add(new TimeTypeHandler(this));
    getDefinedTypeHandlers().add(new DateTypeHandler(this));
    getDefinedTypeHandlers().add(new CalendarTypeHandler(this));
    getDefinedTypeHandlers().add(new CharSequenceTypeHandler(this));
    getDefinedTypeHandlers().add(new ByteTypeHandler(this));
    getDefinedTypeHandlers().add(new URITypeHandler(this));
    getDefinedTypeHandlers().add(new URLTypeHandler(this));
    getDefinedTypeHandlers().add(new ClassTypeHandler(this));
    getDefinedTypeHandlers().add(new LocaleTypeHandler(this));
    getDefinedTypeHandlers().add(new EnumTypeHandler(this));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory#getDefaultTypeHandler()
   */
  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    return defaultHandler;
  }

}
