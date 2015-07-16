/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.json.typehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.FloatTypehandler;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * Creates {@link ITypeHandler} which are creating Json-usable formats from Objects and back
 * 
 * @author Michael Remme
 * 
 */

public class JsonTypeHandlerFactory implements ITypeHandlerFactory {
  private static final ITypeHandler defaultHandler = new ObjectTypeHandler();
  private static final List<ITypeHandler> definedTypeHandlers = new ArrayList<ITypeHandler>();

  /**
   * If for a class a {@link ITypeHandler} was requested and found, it is cached by here with the class to handle as key
   */
  private final Map<Class<?>, ITypeHandler> cachedTypeHandler = new HashMap<Class<?>, ITypeHandler>();

  static {
    definedTypeHandlers.add(new FloatTypehandler());

  }

  /**
   * 
   */
  public JsonTypeHandlerFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory#getTypeHandler(de.braintags.io.vertx.pojomapper
   * .mapping.IField)
   */
  @Override
  public ITypeHandler getTypeHandler(IField field) {
    if (field.getPropertyMapper() instanceof IReferencedMapper || field.getPropertyMapper() instanceof IEmbeddedMapper)
      return null;
    Class<?> fieldClass = field.getType();
    if (cachedTypeHandler.containsKey(fieldClass))
      return cachedTypeHandler.get(fieldClass);
    ITypeHandler handler = examineExcactMatch(fieldClass);
    if (handler == null)
      handler = examineInstanceOfMatch(fieldClass);
    if (handler == null)
      handler = defaultHandler;
    cachedTypeHandler.put(fieldClass, handler);
    return handler;
  }

  private ITypeHandler examineExcactMatch(Class<?> cls) {
    for (ITypeHandler th : definedTypeHandlers) {
      if (th.matchesExcact(cls))
        return th;
    }
    return null;
  }

  private ITypeHandler examineInstanceOfMatch(Class<?> cls) {
    for (ITypeHandler th : definedTypeHandlers) {
      if (th.matchesInstance(cls))
        return th;
    }
    return null;
  }

}
