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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * An interface which describes an Iterator with integrated {@link Handler} callbacks
 * 
 * @author Michael Remme
 * 
 */

public interface IteratorAsync<E> {

  /**
   * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link #next}
   * would return an element rather than throwing an exception.)
   *
   * @return {@code true} if the iteration has more elements
   */
  boolean hasNext();

  /**
   * Returns the next element in the iteration into the {@link Handler}, or a NoSuchElementException, if no more
   * elements exist.
   *
   * @param handler
   *          the handler to be recalled
   */
  void next(Handler<AsyncResult<E>> handler);

}
