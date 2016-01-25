/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractStoreObjectFactory implements IStoreObjectFactory {

  /**
   * 
   */
  public AbstractStoreObjectFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.util.pojomapper.mapping.IStoreObjectFactory#createStoreObjects(de.braintags.io.vertx.util.pojomapper.
   * mapping.IMapper, java.util.List, io.vertx.core.Handler)
   */
  @Override
  public void createStoreObjects(IMapper mapper, List<?> entities,
      Handler<AsyncResult<List<IStoreObject<?>>>> handler) {
    CounterObject<List<IStoreObject<?>>> co = new CounterObject<>(entities.size(), handler);
    List<IStoreObject<?>> returnList = new ArrayList<IStoreObject<?>>();
    for (Object entity : entities) {
      createStoreObject(mapper, entity, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
        } else {
          returnList.add(result.result());
          if (co.reduce()) {
            handler.handle(Future.succeededFuture(returnList));
          }
        }

      });
      if (co.isError()) {
        return;
      }
    }

  }

}
