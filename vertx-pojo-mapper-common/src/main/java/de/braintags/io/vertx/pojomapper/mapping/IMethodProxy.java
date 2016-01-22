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
package de.braintags.io.vertx.pojomapper.mapping;

import java.lang.reflect.Method;

/**
 * A wrapper for a {@link Method}
 * 
 * @author Michael Remme
 * 
 */
public interface IMethodProxy {

  /**
   * Get the underlaying method
   * 
   * @return the method
   */
  public Method getMethod();

  /**
   * Get the prepared parameters for a method call
   * 
   * @return
   */
  public Object[] getParameterValues();
}
