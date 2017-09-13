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
package de.braintags.vertx.jomnigate.dataaccess.query.stream;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

/**
 * An implementation of ReadStream, which serializes each instance as Json. Per instance one line is written
 * 
 * @author Michael Remme
 * 
 */
public class QueryReadStreamBuffer<T> extends QueryReadStream<T, Buffer> {

  public QueryReadStreamBuffer(final IQuery<T> query) {
    super(query);
  }

  public QueryReadStreamBuffer(final IQuery<T> query, final int blockSize) {
    super(query, blockSize);
  }

  @Override
  protected void append(final Handler<Buffer> handler, final T entity) {
    Buffer buffer = Json.encodeToBuffer(entity);
    buffer.appendString("\n");
    handler.handle(buffer);
  }

}
