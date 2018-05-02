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
import java.util.concurrent.atomic.AtomicBoolean;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.util.QueryHelper;
import de.braintags.vertx.util.ExceptionUtil;
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
  private Handler<U> contentHandler;
  private Handler<Throwable> exceptionHandler = new DefaultExceptionHandler();
  private Handler<Void> endHandler;
  private Iterator<T> queryResult;
  private int nextStartPosition = 0;
  private IStreamResult<T> streamResult = new DefaultStreamResult<>();

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
    this.exceptionHandler = handler;
    return this;
  }

  @Override
  public ReadStream<U> handler(@Nullable final Handler<U> handler) {
    this.contentHandler = handler;
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
    LOGGER.debug("called resume");
    paused.set(false);
    start();
    return this;
  }

  private void start() {
    Future<Void> rootFuture = Future.future();
    loop(rootFuture);
    rootFuture.setHandler(res -> {
      if (res.failed()) {
        ended.set(true);
        exceptionHandler.handle(res.cause());
        close();
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
    });
  }

  private void loop(final Future<Void> parentFuture) {
    query.execute(null, blockSize, nextStartPosition, qres -> {
      if (qres.failed()) {
        parentFuture.fail(qres.cause());
      } else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("executed query with startPosition " + nextStartPosition + ": " + qres.result().size());
        }
        setNextStartPosition(qres.result());
        QueryHelper.queryResultToList(qres.result(), lres -> {
          if (lres.failed()) {
            parentFuture.fail(lres.cause());
          } else {
            try {
              queryResult = lres.result().iterator();
              LOGGER.debug("set next result");
              subLoop();
              if (nextStartPosition > 0) {
                loop(parentFuture);
              } else {
                parentFuture.complete();
              }
            } catch (Exception e) {
              parentFuture.fail(e);
            }
          }
        });
      }
    });
  }

  private void subLoop() {
    while (queryResult.hasNext()) {
      T entity = queryResult.next();
      try {
        append(contentHandler, entity);
        getStreamResult().succeededEntity(entity);
      } catch (Throwable e) {
        getStreamResult().failedEntity(entity, e);
      }
    }
  }

  /**
   * Get the instance of IStreamResult, which contains the log of the execution
   * 
   * @return
   */
  public IStreamResult<T> getStreamResult() {
    return streamResult;
  }

  /**
   * The default implementation of IStreamResult used here is {@link DefaultStreamResult}. If you want to add a specific
   * solution, you have to add it here before start of execution
   * 
   * @param streamResult
   */
  public void setStreamResult(final IStreamResult<T> streamResult) {
    this.streamResult = streamResult;
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

  /**
   * Append the given instance to the result
   * 
   * @param handler
   * @param entity
   */
  protected abstract void append(Handler<U> handler, T entity);

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

  class DefaultExceptionHandler implements Handler<Throwable> {

    @Override
    public void handle(final Throwable t) {
      LOGGER.error("", t);
      throw ExceptionUtil.createRuntimeException(t);
    }

  }

}
