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

package de.braintags.io.vertx.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Several utility methods for handling classes
 * 
 * @author Michael Remme
 * 
 */

public class ClassUtil {

  /**
   * Get a list of all methods declared in the supplied class, and all its superclasses (except java.lang.Object),
   * recursively.
   *
   * @param type
   *          the class for which we want to retrieve the Methods
   * @return an array of all declared and inherited fields
   */
  public static List<Method> getDeclaredAndInheritedMethods(final Class<?> type) {
    return getDeclaredAndInheritedMethods(type, new ArrayList<Method>());
  }

  private static List<Method> getDeclaredAndInheritedMethods(final Class<?> type, final List<Method> methods) {
    if ((type == null) || (type == Object.class)) {
      return methods;
    }

    final Class<?> parent = type.getSuperclass();
    final List<Method> list = getDeclaredAndInheritedMethods(parent, methods == null ? new ArrayList<Method>()
        : methods);

    for (final Method m : type.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) {
        list.add(m);
      }
    }

    return list;
  }

}
