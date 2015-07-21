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

package de.braintags.io.vertx.pojomapper.typehandler;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.util.ClassUtil;

/**
 * Abstract implementation of {@link ITypeHandler} which handles
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractTypeHandler implements ITypeHandler {
  private final List<Class<?>> classesToHandle;

  public AbstractTypeHandler(Class<?>... classesToDeal) {
    classesToHandle = Arrays.asList(classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#matchesExcact(java.lang.Class)
   */
  @Override
  public short matches(IField field) {
    return matches(field.getType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#matches(java.lang.Class)
   */
  @Override
  public short matches(Class<?> cls) {
    for (Class<?> dCls : classesToHandle) {
      if (dCls.equals(cls))
        return MATCH_MAJOR;
    }
    for (Class<?> dCls : classesToHandle) {
      if (dCls.isAssignableFrom(cls))
        return MATCH_MINOR;
    }
    return MATCH_NONE;
  }

  /**
   * Get a fitting constructor
   * 
   * @param field
   *          the field to be used
   * @param cls
   *          if field is null, then the class will be used as source
   * @param arguments
   *          the arguments for the constructor
   * @return a fitting {@link Constructor}
   */
  public Constructor<?> getConstructor(IField field, Class<?> cls, Class<?>... arguments) {
    if (field != null) {
      Constructor<?> constr = field.getConstructor(String.class);
      if (constr == null)
        throw new MappingException("Contructor not found with String as parameter for field " + field.getFullName());
      return constr;
    }
    return ClassUtil.getConstructor(cls, arguments);
  }

}
