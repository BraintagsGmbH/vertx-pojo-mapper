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

import java.util.Collection;

/**
 * Helper class to calculate size
 * 
 * @author Michael Remme
 * 
 */

public class Size {

  /**
   * Get the size of an {@link Iterable}
   * 
   * @param data
   * @return
   */
  public static int size(Iterable<?> data) {
    if (data instanceof Collection) {
      return ((Collection<?>) data).size();
    }

    int counter = 0;
    for (Object i : data) {
      counter++;
    }
    return counter;
  }

}
