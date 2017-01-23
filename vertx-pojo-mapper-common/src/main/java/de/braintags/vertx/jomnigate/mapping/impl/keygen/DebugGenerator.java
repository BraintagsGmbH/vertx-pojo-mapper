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
package de.braintags.vertx.jomnigate.mapping.impl.keygen;

import java.util.concurrent.atomic.AtomicLong;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * The {@link DebugGenerator} is reset to 0 by each start of an IDataStore and can be used for testing. It generates an
 * identifyer as long
 * 
 * @author Michael Remme
 * 
 */
public class DebugGenerator extends AbstractKeyGenerator {
  public static final String NAME = "DEBUG";
  private final AtomicLong counter = new AtomicLong(0);

  /**
   * @param name
   * @param datastore
   */
  public DebugGenerator(IDataStore datastore) {
    super(NAME, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IKeyGenerator#generateKey()
   */
  @Override
  public void generateKey(IMapper<?> mapper, Handler<AsyncResult<Key>> handler) {
    handler.handle(Future.succeededFuture(new Key(counter.incrementAndGet())));
  }

  public void resetCounter() {
    counter.set(0);
  }

}
