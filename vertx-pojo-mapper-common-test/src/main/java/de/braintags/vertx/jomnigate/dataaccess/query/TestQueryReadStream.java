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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.stream.QueryReadStreamBuffer;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.ReferenceMapper_Array;
import de.braintags.vertx.util.ExceptionUtil;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
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

  @Test
  public void testStream_Error(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    QueryReadStreamError<MiniMapper> qr = new QueryReadStreamError<>(q);
    execute(context, qr, false, 0, recCount);
  }

  @Test
  public void testStream_BlockSizeEqualsRecordCount(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    QueryReadStreamBuffer<MiniMapper> qr = new QueryReadStreamBuffer<>(q, 100);
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

  @Test
  public void testStream_Embedded(final TestContext context) throws IOException {
    clearTable(context, ReferenceMapper_Array.class.getSimpleName());
    List<ReferenceMapper_Array> rl = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      rl.add(new ReferenceMapper_Array(3));
    }
    saveRecords(context, rl);
    IQuery<ReferenceMapper_Array> q = getDataStore(context).createQuery(ReferenceMapper_Array.class);
    QueryReadStreamBuffer<ReferenceMapper_Array> qr = new QueryReadStreamBuffer<>(q);
    execute(context, qr, true, 5);
  }

  @Test
  public void testStream_Embedded_File(final TestContext context) throws IOException {
    clearTable(context, ReferenceMapper_Array.class.getSimpleName());
    List<ReferenceMapper_Array> rl = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      rl.add(new ReferenceMapper_Array(3));
    }
    saveRecords(context, rl);
    IQuery<ReferenceMapper_Array> q = getDataStore(context).createQuery(ReferenceMapper_Array.class);
    QueryReadStreamBuffer<ReferenceMapper_Array> qr = new QueryReadStreamBuffer<>(q);
    executeFile(context, qr, true);
  }

  @Test
  public void testStreamEmptySelection(final TestContext context) throws IOException {
    IQuery<MiniMapper> q = getDataStore(context).createQuery(MiniMapper.class);
    q.setSearchCondition(ISearchCondition.condition("name", QueryOperator.EQUALS, "NOT_EXISTS"));
    QueryReadStreamBuffer<MiniMapper> qr = new QueryReadStreamBuffer<>(q);
    execute(context, qr, false, 0);
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

  @SuppressWarnings("rawtypes")
  private void execute(final TestContext context, final QueryReadStreamBuffer qr) {
    execute(context, qr, true, recCount);
  }

  @SuppressWarnings("rawtypes")
  private void executeFile(final TestContext context, final QueryReadStreamBuffer qr,
      final boolean expectBufferWritten) {
    try {
      String path = File.createTempFile("dumpRecords", ".json").getAbsolutePath();
      LOGGER.debug("writing into " + path);
      OpenOptions options = new OpenOptions();
      AsyncFile af = getDataStore(context).getVertx().fileSystem().openBlocking(path, options);
      execute(context, qr, af);
      Buffer buffer = getDataStore(context).getVertx().fileSystem().readFileBlocking(path);
      if (expectBufferWritten) {
        context.assertTrue(buffer.length() > 0, "buffer was not written");
      } else {
        context.assertFalse(buffer.length() > 0, "buffer should not be written");
      }
      LOGGER.debug(buffer);
    } catch (IOException e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  @SuppressWarnings("rawtypes")
  private void execute(final TestContext context, final QueryReadStreamBuffer qr,
      final boolean expectBufferWritten, final int recCount) {
    execute(context, qr, expectBufferWritten, recCount, 0);
  }

  @SuppressWarnings("rawtypes")
  private void execute(final TestContext context, final QueryReadStreamBuffer qr,
      final boolean expectBufferWritten, final int succeededCount, final int failedCount) {
    BufferWriteStream ws = new BufferWriteStream();
    execute(context, qr, ws);
    if (expectBufferWritten) {
      context.assertTrue(ws.buffer.length() > 0, "buffer was not written");
    } else {
      context.assertFalse(ws.buffer.length() > 0, "buffer should not be written");
    }
    context.assertEquals(succeededCount, ws.count, "not all instances were written");
    context.assertEquals(succeededCount, qr.getStreamResult().getSucceeded(), "not all instances were written");
    context.assertEquals(failedCount, qr.getStreamResult().getFailed(), "failed instances not correct");

    LOGGER.debug(ws.buffer);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void execute(final TestContext context, final QueryReadStreamBuffer qr, final WriteStream ws) {
    Async async = context.async();
    qr.endHandler(end -> async.complete());
    qr.exceptionHandler(new ErrorHandler(context, async));
    Pump p = Pump.pump(qr, ws);
    p.start();
    async.await();
  }

  public static class ErrorHandler implements Handler<Throwable> {
    private final TestContext context;
    private final Async async;

    ErrorHandler(final TestContext context, final Async async) {
      this.context = context;
      this.async = async;
    }

    @Override
    public void handle(final Throwable cause) {
      async.complete();
      context.fail(cause);
    }

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
