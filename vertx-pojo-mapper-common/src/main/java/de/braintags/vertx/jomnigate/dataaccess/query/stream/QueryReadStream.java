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
package de.braintags.vertx.jomnigate.dataaccess.query.stream;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.util.QueryHelper;
import de.braintags.vertx.util.ResultObject;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

/**
 * an implementation of {@link ReadStream} which streams a query result into the required format
 * 
 * @author Michael Remme
 * @param <T>
 *          the mapper class to be handled
 * @param <U>
 *          the type of the result
 */
public abstract class QueryReadStream<T, U> implements ReadStream<U> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(QueryReadStream.class);

  private final IQuery<T> query;
  private int blockSize = 2000;
  private Handler<U> handler;
  private Handler<Void> endHandler;
  private Iterator<T> queryResult;
  private int nextStartPosition = 0;

  private final AtomicBoolean paused = new AtomicBoolean(false);
  private final AtomicBoolean ended = new AtomicBoolean(false);

  /**
   * Constructor which is usind the default block size of 2000
   * 
   * @param query
   */
  public QueryReadStream(final IQuery<T> query) {
    this(query, 2000);
  }

  /**
   * execute queries with the given block size
   * 
   * @param query
   * @param blockSize
   *          the maximum number of records per query
   */
  public QueryReadStream(final IQuery<T> query, final int blockSize) {
    this.query = query;
    this.blockSize = blockSize;
  }

  @Override
  public ReadStream<U> exceptionHandler(final Handler<Throwable> handler) {
    return this;
  }

  @Override
  public ReadStream<U> handler(@Nullable final Handler<U> handler) {
    this.handler = handler;
    resume();
    return this;
  }

  @Override
  public ReadStream<U> pause() {
    paused.set(true);
    return this;
  }

  @Override
  public ReadStream<U> resume() {
    paused.set(false);
    nextRow();
    return this;
  }

  @Override
  public ReadStream<U> endHandler(@Nullable final Handler<Void> endHandler) {
    this.endHandler = endHandler;
    // registration was late but we're already ended, notify
    if (ended.compareAndSet(true, false)) {
      // only notify once
      endHandler.handle(null);
    }
    return this;
  }

  private void nextRow() {
    if (!paused.get()) {
      T next = getNext();
      if (next != null) {
        append(handler, next);
        nextRow();
      } else {
        // mark as ended if the handler was registered too late
        ended.set(true);
        // automatically close resources
        close(c -> {
          if (endHandler != null) {
            endHandler.handle(null);
          }
        });
      }
    }
  }

  /**
   * Append the given instance to the result
   * 
   * @param handler
   * @param entity
   */
  protected abstract void append(Handler<U> handler, T entity);

  private T getNext() {
    if (getQueryResult().hasNext()) {
      return getQueryResult().next();
    } else {
      return null;
    }
  }

  private Iterator<T> getQueryResult() {
    if (queryResult == null || (!queryResult.hasNext() && nextStartPosition > -1)) {
      CountDownLatch latch = new CountDownLatch(1);
      ResultObject<List<T>> ro = new ResultObject<>(null);
      query.execute(null, blockSize, nextStartPosition, qres -> {
        if (qres.failed()) {
          ro.setThrowable(qres.cause());
          latch.countDown();
        } else {
          setNextStartPosition(qres.result());
          createResult(latch, ro, qres.result());
        }
      });

      try {
        latch.await();
      } catch (InterruptedException e) {
        LOGGER.error("error in latch await", e);
      }
      if (ro.isError()) {
        throw ro.getRuntimeException();
      } else {
        queryResult = ro.getResult().iterator();
      }
    }
    return queryResult;
  }

  private void createResult(final CountDownLatch latch, final ResultObject<List<T>> ro, final IQueryResult<T> qr) {
    QueryHelper.queryResultToList(qr, res -> {
      if (res.failed()) {
        ro.setThrowable(res.cause());
      } else {
        ro.setResult(res.result());
      }
      latch.countDown();
    });
  }

  private void setNextStartPosition(final IQueryResult<T> res) {
    nextStartPosition = res.size() >= blockSize ? nextStartPosition + blockSize : -1;
  }

  /**
   * Close the stream
   */
  public void close() {
    close(null);
  }

  /**
   * Close the stream and inform the handler
   * 
   * @param handler
   */
  public void close(final Handler<AsyncResult<Void>> handler) {
    // make sure we stop pumping data
    pause();
    // call the provided handler
    if (handler != null) {
      handler.handle(Future.succeededFuture());
    }
  }

}
