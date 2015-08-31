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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Collection;

/**
 * A collection, which integrates {@link Handler} callbacks in certain methods
 * 
 * @author Michael Remme
 * 
 */

public interface CollectionAsync<E> {

  /**
   * Returns the number of elements in this collection. If this collection contains more than <tt>Integer.MAX_VALUE</tt>
   * elements, returns <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of elements in this collection
   */
  int size();

  /**
   * Returns <tt>true</tt> if this collection contains no elements.
   *
   * @return <tt>true</tt> if this collection contains no elements
   */
  boolean isEmpty();

  /**
   * Returns <tt>true</tt> if this collection contains the specified element. More formally, returns <tt>true</tt> if
   * and only if this collection contains at least one element <tt>e</tt> such that
   * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
   *
   * @param o
   *          element whose presence in this collection is to be tested
   * @param handler
   *          the handler to be recalled
   * @throws ClassCastException
   *           if the type of the specified element is incompatible with this collection (<a
   *           href="#optional-restrictions">optional</a>)
   * @throws NullPointerException
   *           if the specified element is null and this collection does not permit null elements (<a
   *           href="#optional-restrictions">optional</a>)
   */
  void contains(Object o, Handler<AsyncResult<Boolean>> handler);

  /**
   * Returns an iterator over the elements in this collection. There are no guarantees concerning the order in which the
   * elements are returned (unless this collection is an instance of some class that provides a guarantee).
   *
   * @return an <tt>Iterator</tt> over the elements in this collection
   */
  IteratorAsync<E> iterator();

  /**
   * Returns an array containing all of the elements in this collection. If this collection makes any guarantees as to
   * what order its elements are returned by its iterator, this method must return the elements in the same order.
   *
   * <p>
   * The returned array will be "safe" in that no references to it are maintained by this collection. (In other words,
   * this method must allocate a new array even if this collection is backed by an array). The caller is thus free to
   * modify the returned array.
   *
   * <p>
   * This method acts as bridge between array-based and collection-based APIs.
   *
   * @return an array containing all of the elements in this collection
   */
  void toArray(Handler<AsyncResult<Object[]>> handler);

  /**
   * Adds a new instance
   * 
   * @param e
   *          the instance to be added
   * @return true, if successfully added
   */
  boolean add(E e);

  /**
   * Removes an instance
   * 
   * @param e
   *          the instance to be removed
   * @return true, if successfully removed
   */
  boolean remove(Object o);

  /**
   * Checks wether all elements are contained in the current instance
   * 
   * @param c
   *          the elements to be checked
   * @param handler
   *          the handler to be recalled
   */
  void containsAll(CollectionAsync<?> c, Handler<AsyncResult<Boolean>> handler);

  /**
   * Add all members of the given collection
   * 
   * @param c
   *          the objects to be added
   * @return true, if all members were added
   */
  boolean addAll(CollectionAsync<? extends E> c);

  /**
   * Remove all elements of the given Collection
   * 
   * @param c
   *          the elements to be removed
   * @return true, if succcesfully removed
   */
  boolean removeAll(Collection<?> c);

  /**
   * clear the current instance
   */
  void clear();

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

}
