/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.util.AbstractCollectionAsync;
import de.braintags.vertx.util.IteratorAsync;
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

public abstract class AbstractQueryResult<T> extends AbstractCollectionAsync<T> implements IQueryResult<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractQueryResult.class);

  private IMapper<T> mapper;
  private IDataStore datastore;
  private T[] pojoResult;
  private IQueryExpression originalQuery;
  private long completeResult;

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
  public AbstractQueryResult(IDataStore datastore, IMapper<T> mapper, int resultSize, IQueryExpression originalQuery) {
    this.datastore = datastore;
    this.mapper = mapper;
    this.originalQuery = originalQuery;
    this.pojoResult = (T[]) new Object[resultSize];
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.util.util.CollectionAsync#size()
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
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult#getMapper()
   */
  @Override
  public IMapper<T> getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult#getOriginalQuery()
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
      int thisIndex = currentIndex++;
      if (pojoResult[thisIndex] == null) {
        LOGGER
            .debug("generating pojo on index " + thisIndex + " for mapper " + mapper.getMapperClass().getSimpleName());
        generatePojo(thisIndex, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            pojoResult[thisIndex] = result.result();
            handler.handle(Future.succeededFuture(pojoResult[thisIndex]));
          }
        });
      } else {
        LOGGER.debug("reusing pojo on index " + thisIndex + " for mapper " + mapper.getMapperClass().getSimpleName());
        handler.handle(Future.succeededFuture(pojoResult[thisIndex]));
      }
    }

  }

  /**
   * @return the completeResult
   */
  @Override
  public final long getCompleteResult() {
    return completeResult;
  }

  /**
   * @param completeResult
   *          the completeResult to set
   */
  public final void setCompleteResult(long completeResult) {
    this.completeResult = completeResult;
  }

  @Override
  public String toString() {
    return String.valueOf(originalQuery);
  }

}
