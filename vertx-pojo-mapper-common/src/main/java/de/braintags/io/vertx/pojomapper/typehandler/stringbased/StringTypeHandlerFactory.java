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

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.CharacterTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ObjectTypeHandler;

/**
 * Creates {@link ITypeHandler} which are creating String from Objects and back
 * 
 * @author Michael Remme
 * 
 */

public class StringTypeHandlerFactory extends AbstractTypeHandlerFactory {
  private static final ITypeHandler defaultHandler = new ObjectTypeHandler();
  private static final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  static {
    definedTypeHandlers.add(new CharacterTypeHandler());

  }

  /**
   * 
   */
  public StringTypeHandlerFactory() {
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
