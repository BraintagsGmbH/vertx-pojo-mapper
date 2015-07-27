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

/**
 * A helper to count loops inside an asynchron call
 * 
 * @author Michael Remme
 * 
 */

public class CounterObject extends Object {
  private int count;

  /**
   * 
   */
  public CounterObject(int count) {
    this.count = count;
  }

  /**
   * Reduces the counter by 1 and returns true, if the counter reached 0
   * 
   * @return true, if zero
   */
  public boolean reduce() {
    --count;
    return count == 0;
  }
}
