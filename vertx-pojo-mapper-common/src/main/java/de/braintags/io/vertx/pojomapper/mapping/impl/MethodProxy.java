/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.lang.reflect.Method;

import de.braintags.io.vertx.pojomapper.mapping.IMethodProxy;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContext;

/**
 * Proxy for encapsulated Methods
 * 
 * 
 * @author Michael Remme
 *
 */
class MethodProxy implements IMethodProxy {
  Method method;
  Class<?>[] parameterTypes;

  public MethodProxy(Method method, Mapper mapper) {
    this.method = method;
    if (method.getParameterTypes().length > 0) {
      compute(mapper);
    }
  }

  private void compute(Mapper mapper) {
    parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 1) {
      throw new UnsupportedOperationException("only 1 parameter supported");
    }
    if (parameterTypes != null) {
      for (Class<?> pt : parameterTypes) {
        checkParameter(pt);
      }
    }
  }

  private void checkParameter(Class<?> param) {
    if (ITriggerContext.class.isAssignableFrom(param)) {
      return;
    }
    throw new UnsupportedOperationException("Unsupported Parameter, only ITriggerContext allowed");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMethodProxy#getMethod()
   */
  @Override
  public Method getMethod() {
    return method;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getMethodString().hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    return getMethodString().equals(((MethodProxy) obj).getMethodString());
  }

  private String getMethodString() {
    String ms = method.getDeclaringClass().getName() + method.getName();
    if (parameterTypes != null) {
      for (Class<?> cls : parameterTypes) {
        ms += cls.getName();
      }
    }
    return ms;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMethodProxy#getParameterTypes()
   */
  @Override
  public Object[] getParameterTypes() {
    return parameterTypes;
  }

}
