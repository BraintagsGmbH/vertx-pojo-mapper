/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */
package de.braintags.io.vertx.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class ReflectionUtil {

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
      for (int i = 0; i < values.size(); i++) {
        Array.set(exampleArray, i, values.get(i));
      }
      return exampleArray;
    }
  }

}
