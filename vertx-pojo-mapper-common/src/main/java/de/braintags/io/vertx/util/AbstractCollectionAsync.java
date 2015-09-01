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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.groovy.core.Future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The abstract implementation defines an unmodifyable list, cause all methods, which are changing the content are
 * throwing an {@link UnsupportedOperationException}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractCollectionAsync<E> implements CollectionAsync<E> {

  /**
   * 
   */
  public AbstractCollectionAsync() {
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public void contains(Object o, Handler<AsyncResult<Boolean>> handler) {
    IteratorAsync<E> it = iterator();
    ResultObject<Boolean> ro = new ResultObject<Boolean>();
    while (it.hasNext()) {
      it.next(result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          if (o == null && result.result() == null) {
            ro.setResult(true);
          } else if (o.equals(result.result())) {
            ro.setResult(true);
          }
        }
      });
      if (ro.handleResult(handler))
        return;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#toArray(io.vertx.core.Handler)
   */
  @Override
  public void toArray(Handler<AsyncResult<Object[]>> handler) {
    List<Object> list = new ArrayList<Object>();
    if (isEmpty()) {
      handler.handle((AsyncResult<Object[]>) Future.succeededFuture(list.toArray()));
      return;
    }
    CounterObject co = new CounterObject(size());
    IteratorAsync<E> it = iterator();
    ResultObject<Object[]> ro = new ResultObject<Object[]>();
    while (it.hasNext()) {
      it.next(result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          list.add(result.result());
          if (co.reduce()) {
            ro.setResult(list.toArray());
            ro.handleResult(handler);
          }
        }
      });
      if (ro.isError()) {
        ro.handleResult(handler);
        return;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#add(java.lang.Object)
   */
  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#remove(java.lang.Object)
   */
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync,
   * io.vertx.core.Handler)
   */
  @Override
  public void containsAll(CollectionAsync<?> c, Handler<AsyncResult<Boolean>> handler) {
    IteratorAsync<?> it = c.iterator();
    ResultObject<Boolean> ro = new ResultObject<Boolean>();
    while (it.hasNext()) {
      it.next(result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          Object check = result.result();
          contains(check, checkResult -> {
            if (checkResult.failed()) {
              ro.setThrowable(checkResult.cause());
            } else {
              if (!checkResult.result())
                ro.setResult(false);
            }
          });
        }
      });
      if (ro.handleResult(handler))
        return;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#addAll(de.braintags.io.vertx.util.CollectionAsync)
   */
  @Override
  public boolean addAll(CollectionAsync<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#clear()
   */
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

}
