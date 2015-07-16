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

import java.util.Arrays;
import java.util.List;

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
  public boolean matchesExcact(Class<?> cls) {
    for (Class<?> dCls : classesToHandle) {
      if (dCls.equals(cls))
        return true;
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#matchesInstance(java.lang.Class)
   */
  @Override
  public boolean matchesInstance(Class<?> cls) {
    for (Class<?> dCls : classesToHandle) {
      if (dCls.isAssignableFrom(cls))
        return true;
    }
    return false;
  }

}
