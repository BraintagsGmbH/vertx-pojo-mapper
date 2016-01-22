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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.lang.reflect.Method;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IMethodProxy;
import de.braintags.io.vertx.util.ClassUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

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
  Object[] parameterValues;

  public MethodProxy(Method method) {
    this.method = method;
    parameterTypes = method.getParameterTypes();
  }

  public void computeParameterValues(Mapper mapper) {
    if (parameterTypes != null) {
      for (Class<?> pt : parameterTypes) {
        checkParameter(pt);
      }

      if (parameterTypes.length == 1) {
        if (parameterTypes[0] == IDataStore.class) {
          parameterValues = new Object[] { mapper.getMapperFactory().getDataStore() };
        } else {
          throw new UnsupportedOperationException("only IDataStore as parameter supported");
        }
      } else if (parameterTypes.length > 1) {
        throw new UnsupportedOperationException("only 1 parameter supported");
      }
    }
  }

  private void checkParameter(Class<?> param) {
    if (IDataStore.class.isAssignableFrom(param)) {
      return;
    }
    if (Handler.class.isAssignableFrom(param)) {
      Class<?> sub = ClassUtil.getParameterizedClass(param);
      if (AsyncResult.class.isAssignableFrom(sub)) {
        Class<?> subSub = ClassUtil.getParameterizedClass(sub);
        if (Void.class.isAssignableFrom(subSub)) {
          return;
        }
      }
      throw new UnsupportedOperationException("Handler<AsyncResult<Void>> is required as parameter");
    }
    throw new UnsupportedOperationException(
        "Unsupported Parameter, allowed are IDataStore and Handler<AsyncResult<Void>>");
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
   * @see de.braintags.io.vertx.pojomapper.mapping.IMethodProxy#getParameterValues()
   */
  @Override
  public Object[] getParameterValues() {
    return parameterValues;
  }

  public void setParameterValues(Object[] values) {
    this.parameterValues = values;
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

}