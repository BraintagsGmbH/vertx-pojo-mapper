/*-
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.dataaccess.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.stream.QueryReadStreamBuffer;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestQueryReadStream extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestQueryReadStream.class);
  private static int recCount = 100;

  @Test(expected = IllegalArgumentException.class)
  public void testStream_Error(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    QueryReadStreamError<MiniMapper> qr = new QueryReadStreamError<>(q);
    execute(context, qr);
  }

  @Test
  public void testStream_BlockSize(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    QueryReadStreamBuffer<MiniMapper> qr = new QueryReadStreamBuffer<>(q, 10);
    execute(context, qr);
  }

  @Test
  public void testStream(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    QueryReadStreamBuffer<MiniMapper> qr = new QueryReadStreamBuffer<>(q);
    execute(context, qr);
  }

  @BeforeClass
  public static final void beforeClass(final TestContext context) {
    clearTable(context, MiniMapper.class);
    List<MiniMapper> ml = new ArrayList<>();
    for (int i = 0; i < recCount; i++) {
      ml.add(new MiniMapper("test " + i));
    }
    saveRecords(context, ml);
  }

  private void execute(final TestContext context, final QueryReadStreamBuffer<MiniMapper> qr) {
    Async async = context.async();
    qr.endHandler(end -> async.complete());
    BufferWriteStream ws = new BufferWriteStream();
    Pump p = Pump.pump(qr, ws);
    p.start();
    async.await();
    context.assertTrue(ws.buffer.length() > 0, "buffer was not written");
    context.assertEquals(recCount, ws.count, "not all instances were written");
    LOGGER.debug(ws.buffer);
  }

  public static class QueryReadStreamError<T> extends QueryReadStreamBuffer<T> {

    public QueryReadStreamError(final IQuery<T> query) {
      super(query);
    }

    public QueryReadStreamError(final IQuery<T> query, final int blockSize) {
      super(query, blockSize);
    }

    @Override
    protected void append(final Handler<Buffer> handler, final T entity) {
      throw new IllegalArgumentException("testexeption");
    }

  }

  /**
   * Writes content into a Buffer and counts the write actions
   * 
   * 
   * @author Michael Remme
   *
   */
  public static class BufferWriteStream implements WriteStream<Buffer> {
    private final Buffer buffer = Buffer.buffer();
    private Handler<Throwable> exceptionHandler;
    private int count;

    @Override
    public WriteStream<Buffer> exceptionHandler(final Handler<Throwable> handler) {
      this.exceptionHandler = handler;
      return this;
    }

    @Override
    public WriteStream<Buffer> write(final Buffer data) {
      ++count;
      buffer.appendBuffer(data);
      return this;
    }

    @Override
    public WriteStream<Buffer> setWriteQueueMaxSize(final int maxSize) {
      return this;
    }

    @Override
    public boolean writeQueueFull() {
      return false;
    }

    @Override
    public WriteStream<Buffer> drainHandler(final Handler<Void> handler) {
      return this;
    }

    @Override
    public void end() {
    }

  }
}
