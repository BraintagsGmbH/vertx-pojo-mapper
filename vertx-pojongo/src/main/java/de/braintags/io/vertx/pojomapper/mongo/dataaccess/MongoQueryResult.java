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

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.json.JsonObject;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryResult<T> extends AbstractCollection<T> implements IQueryResult<T> {
  @SuppressWarnings("unused")
  private MongoDataStore store;

  /**
   * Contains the original result from mongo
   */
  private List<JsonObject> jsonResult;
  private T[] pojoResult;

  /**
   * 
   */
  public MongoQueryResult(List<JsonObject> jsonResult, MongoDataStore store) {
    this.store = store;
    this.jsonResult = jsonResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractCollection#iterator()
   */
  @Override
  public Iterator<T> iterator() {
    return new QueryResultIterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractCollection#size()
   */
  @Override
  public int size() {
    return pojoResult.length;
  }

  private void generatePojo(int i) {
    JsonObject sourceObject = jsonResult.get(i);
    throw new UnsupportedOperationException();
  }

  class QueryResultIterator implements Iterator<T> {
    private int currentIndex = 0;

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return currentIndex < pojoResult.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
      if (pojoResult[currentIndex] == null) {
        generatePojo(currentIndex);
      }
      return pojoResult[currentIndex++];
    }

  }
}
