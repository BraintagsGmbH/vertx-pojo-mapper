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

package de.braintags.io.vertx.pojomapper.mapping;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * An IDataStoreSynchronizer is used during the mapping process to check, wether the underlaying table / collection
 * should be created or modified
 * 
 * @author Michael Remme
 * 
 * @param <T>
 *          the dataformat which is used as sync command
 */
@FunctionalInterface
public interface IDataStoreSynchronizer<T> {

  /**
   * Check wether the underlaying table / column inside the database must be created or modified
   * 
   * @param mapper
   * @param resultHandler
   */
  public void synchronize(IMapper mapper, Handler<AsyncResult<ISyncResult<T>>> resultHandler);

}
