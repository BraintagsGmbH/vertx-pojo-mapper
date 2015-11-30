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

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.util.AbstractCollectionAsync;
import de.braintags.io.vertx.util.IteratorAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * An abstract implementation of IQueryResult. Extensions must implement one method to generate single pojos
 * 
 * @author Michael Remme
 * @param <T>
 *          the class of the mapper, which builds the result
 */

public abstract class AbstractQueryResult<T> extends AbstractCollectionAsync<T>implements IQueryResult<T> {
  private IMapper mapper;
  private IDataStore datastore;
  private T[] pojoResult;
  private IQueryExpression originalQuery;

  /**
   * Constructor
   * 
   * @param datastore
   *          the datastore which was used
   * @param mapper
   *          the mapper which was used
   * @param resultSize
   *          the size of the resulting query
   * @param originalQuery
   *          the original query which was processed to create the current result
   */
  @SuppressWarnings("unchecked")
  public AbstractQueryResult(IDataStore datastore, IMapper mapper, int resultSize, IQueryExpression originalQuery) {
    this.datastore = datastore;
    this.mapper = mapper;
    this.originalQuery = originalQuery;
    this.pojoResult = (T[]) new Object[resultSize];
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#size()
   */
  @Override
  public final int size() {
    return pojoResult.length;
  }

  /**
   * Create a Pojo from the information read from the datastore at position i and return it to the handler. The handler
   * will place the object into the internal array at the same position
   * 
   * @param i
   *          the position inside the result from the datastore
   * @param handler
   */
  protected abstract void generatePojo(int i, Handler<AsyncResult<T>> handler);

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getOriginalQuery()
   */
  @Override
  public IQueryExpression getOriginalQuery() {
    return originalQuery;
  }

  @Override
  public IteratorAsync<T> iterator() {
    return new QueryResultIterator();
  }

  class QueryResultIterator implements IteratorAsync<T> {
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < pojoResult.length;
    }

    @Override
    public void next(Handler<AsyncResult<T>> handler) {
      if (pojoResult[currentIndex] == null) {
        generatePojo(currentIndex, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            pojoResult[currentIndex] = result.result();
            handler.handle(Future.succeededFuture(pojoResult[currentIndex++]));
          }
        });
      } else {
        handler.handle(Future.succeededFuture(pojoResult[currentIndex++]));
      }
    }

  }

}
