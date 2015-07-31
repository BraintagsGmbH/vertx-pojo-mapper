/*
 *
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
   *          the Iterable to be examined
   * @return the size of the Iterable. If thge element is null, it will return 0
   */
  public static int size(Iterable<?> data) {
    if (data == null)
      return 0;
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
