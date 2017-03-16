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
package de.braintags.vertx.jomnigate.mapping.impl;

import java.lang.reflect.Method;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;

/**
 * Proxy for encapsulated Methods
 * 
 * 
 * @author Michael Remme
 *
 */
public class MethodProxy implements IMethodProxy {
  private Method method;
  private Class<?>[] parameterTypes;

  public MethodProxy(Method method, IMapper<?> mapper) {
    this.method = method;
    if (method.getParameterTypes().length > 0) {
      compute(mapper);
    }
  }

  private void compute(IMapper<?> mapper) {
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
   * @see de.braintags.vertx.jomnigate.mapping.IMethodProxy#getMethod()
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
   * @see de.braintags.vertx.jomnigate.mapping.IMethodProxy#getParameterTypes()
   */
  @Override
  public Object[] getParameterTypes() {
    return parameterTypes;
  }

}
