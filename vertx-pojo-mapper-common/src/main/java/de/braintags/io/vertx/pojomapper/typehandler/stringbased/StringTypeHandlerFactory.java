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
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.PointTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.PriceTypeHandler;
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
    add(new BooleanTypeHandler(this));
    add(new JsonTypeHandler(this));
    add(new CharacterTypeHandler(this));
    add(new PriceTypeHandler(this));
    add(new BigDecimalTypeHandler(this));
    add(new BigIntegerTypeHandler(this));
    add(new FloatTypeHandler(this));
    add(new DoubleTypeHandler(this));
    add(new ShortTypeHandler(this));
    add(new IntegerTypeHandler(this));
    add(new LongTypeHandler(this));
    add(new TimestampTypeHandler(this));
    add(new TimeTypeHandler(this));
    add(new DateTypeHandler(this));
    add(new CalendarTypeHandler(this));
    add(new CharSequenceTypeHandler(this));
    add(new ByteTypeHandler(this));
    add(new URITypeHandler(this));
    add(new URLTypeHandler(this));
    add(new ClassTypeHandler(this));
    add(new LocaleTypeHandler(this));
    add(new EnumTypeHandler(this));
    add(new PointTypeHandler(this));

  }

  private void add(ITypeHandler th) {
    getDefinedTypeHandlers().add(th);
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
