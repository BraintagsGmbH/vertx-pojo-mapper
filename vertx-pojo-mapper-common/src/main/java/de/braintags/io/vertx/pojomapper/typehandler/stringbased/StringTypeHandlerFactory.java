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
import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BigDecimalTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BigIntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ByteTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CalendarTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CharSequenceTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CharacterTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ClassTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.DateTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.EnumTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.FloatTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.IntegerTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.LocaleTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.LongTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ShortTypeHandler;
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
  private final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  /**
   * The default constructor for a String base factory
   */
  public StringTypeHandlerFactory() {
    init();
  }

  private void init() {
    definedTypeHandlers.add(new CharacterTypeHandler(this));
    definedTypeHandlers.add(new BigDecimalTypeHandler(this));
    definedTypeHandlers.add(new BigIntegerTypeHandler(this));
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
    definedTypeHandlers.add(new ClassTypeHandler(this));
    definedTypeHandlers.add(new LocaleTypeHandler(this));
    definedTypeHandlers.add(new EnumTypeHandler(this));
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
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    return defaultHandler;
  }

}
