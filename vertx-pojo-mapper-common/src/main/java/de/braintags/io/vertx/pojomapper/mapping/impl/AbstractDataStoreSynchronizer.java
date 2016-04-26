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

import de.braintags.io.vertx.pojomapper.annotation.Indexes;
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
      syncTable(mapper, res -> {
        if (res.failed()) {
          resultHandler.handle(Future.failedFuture(res.cause()));
        } else {
          syncIndexDefinitions(mapper, idxResult -> {
            if (idxResult.failed()) {
              resultHandler.handle(Future.failedFuture(idxResult.cause()));
            } else {
              synchronizedInstances.add(mapper.getMapperClass().getName());
              resultHandler.handle(Future.succeededFuture(getSyncResult()));
            }
          });
        }
      });
    }
  }

  /**
   * Check for existing index definitions and sync them if existing. The default implementation calls the method
   * {@link #syncIndexes(IMapper, Indexes, Handler)}
   * 
   * @param mapper
   * @param resultHandler
   */
  protected void syncIndexDefinitions(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    if (mapper.getIndexDefinitions() == null) {
      resultHandler.handle(Future.succeededFuture());
    } else {
      syncIndexes(mapper, mapper.getIndexDefinitions(), resultHandler);
    }
  }

  /**
   * Called to perform the synchronization of the definitions found in {@link Indexes}
   * 
   * @param mapper
   * @param indexes
   * @param resultHandler
   */
  protected abstract void syncIndexes(IMapper mapper, Indexes indexes, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Called if the synchronization wasn't done yet. This method shall perform the synchronization for the table /
   * collection itself and create / update the entity inside the datastore
   * 
   * @param mapper
   * @param resultHandler
   */
  protected abstract void syncTable(IMapper mapper, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get the instance of ISyncResult, which is used by this implementation
   * 
   * @return the sync result
   */
  protected abstract ISyncResult<T> getSyncResult();
}
