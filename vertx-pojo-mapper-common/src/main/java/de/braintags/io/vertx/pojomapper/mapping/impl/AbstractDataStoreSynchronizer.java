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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Abstract implementation for {@link IDataStoreSynchronizer}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreSynchronizer<T> implements IDataStoreSynchronizer<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataStoreSynchronizer.class);
  private List<String> synchronizedInstances = new ArrayList<>();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer#synchronize(de.braintags.io.vertx.pojomapper.
   * mapping .IMapper, io.vertx.core.Handler)
   */
  @Override
  public final void synchronize(IMapper mapper, Handler<AsyncResult<ISyncResult<T>>> resultHandler) {
    if (synchronizedInstances.contains(mapper.getMapperClass().getName())) {
      resultHandler.handle(Future.succeededFuture(getSyncResult()));
    } else {
      LOGGER.debug("starting synchronization for mapper " + mapper.getClass().getSimpleName());
      internalSyncronize(mapper, res -> {
        if (res.failed()) {
          resultHandler.handle(res);
        } else {
          synchronizedInstances.add(mapper.getMapperClass().getName());
          resultHandler.handle(res);
        }
      });
    }
  }

  /**
   * Called if the synchronization wasn't done yet
   * 
   * @param mapper
   * @param resultHandler
   */
  protected abstract void internalSyncronize(IMapper mapper, Handler<AsyncResult<ISyncResult<T>>> resultHandler);

  /**
   * Get the instance of ISyncResult, which is used by this implementation
   * 
   * @return the sync result
   */
  protected abstract ISyncResult<T> getSyncResult();
}
