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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestCollectionAsync<T> extends AbstractCollectionAsync<T> {
  private List<T> internalList;

  /**
   * 
   */
  public TestCollectionAsync(List<T> list) {
    this.internalList = list;
  }

  /**
   * 
   */
  public TestCollectionAsync(T... content) {
    this.internalList = Arrays.asList(content);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.util.CollectionAsync#size()
   */
  @Override
  public int size() {
    return internalList.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.util.CollectionAsync#iterator()
   */
  @Override
  public IteratorAsync<T> iterator() {
    return new TestIterator(internalList.iterator());
  }

  class TestIterator implements IteratorAsync<T> {
    private Iterator<T> it;

    /**
     * 
     */
    public TestIterator(Iterator<T> it) {
      this.it = it;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.util.util.IteratorAsync#hasNext()
     */
    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.util.util.IteratorAsync#next(io.vertx.core.Handler)
     */
    @Override
    public void next(Handler<AsyncResult<T>> handler) {
      Future<T> f = Future.succeededFuture(it.next());
      handler.handle(f);
    }

  }
}
