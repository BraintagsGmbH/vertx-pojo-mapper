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
package de.braintags.io.vertx.util;

import java.lang.reflect.Array;
import java.util.List;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class ReflectionUtil {
  private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

  /**
   * Convert the given list members into an array with the specified type
   * 
   * @param type
   *          the type of the new array
   * @param values
   *          the values to be handled
   * @return the converted array
   */
  public static Object convertToArray(final Class<?> type, final List<?> values) {
    final Object exampleArray = Array.newInstance(type, values.size());
    try {
      return values.toArray((Object[]) exampleArray);
    } catch (ClassCastException e) {
      logger.warn("ClassCastException for toArray - taking the long road");
      for (int i = 0; i < values.size(); i++) {
        Array.set(exampleArray, i, values.get(i));
      }
      return exampleArray;
    }
  }

}
