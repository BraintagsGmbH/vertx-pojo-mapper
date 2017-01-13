/*-
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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractStoreObjectFactory<F> implements IStoreObjectFactory<F> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory#createStoreObjects(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper, java.util.List, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T> void createStoreObjects(IMapper<T> mapper, List<T> entities,
      Handler<AsyncResult<List<IStoreObject<T, ?>>>> handler) {
    List<Future> fl = createFutureList(mapper, entities);
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List stl = createStoreObjectList(cf);
        handler.handle(Future.succeededFuture(stl));
      }
    });
  }

  @SuppressWarnings("unchecked")
  private <T> List<IStoreObject<T, ?>> createStoreObjectList(CompositeFuture cf) {
    List<IStoreObject<T, ?>> stl = new ArrayList<>();
    cf.list().forEach(f -> stl.add((IStoreObject<T, ?>) f));
    return stl;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> List<Future> createFutureList(IMapper<T> mapper, List<T> entities) {
    List<Future> fl = new ArrayList<>();
    for (T entity : entities) {
      Future f = Future.future();
      fl.add(f);
      createStoreObject(mapper, entity, f.completer());
    }
    return fl;
  }

}
