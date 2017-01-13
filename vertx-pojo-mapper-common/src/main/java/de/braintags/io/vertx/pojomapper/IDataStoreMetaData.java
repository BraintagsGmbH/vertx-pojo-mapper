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

package de.braintags.io.vertx.pojomapper;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * The interface gives information about the datastore itself, like the version for instance
 * 
 * @author Michael Remme
 * 
 */

public interface IDataStoreMetaData {

  /**
   * Get the version of the datastore as complete string
   * 
   * @param handler
   *          the handler, which will be called to receive the version number
   */
  public void getVersion(Handler<AsyncResult<String>> handler);

  /**
   * Get information about an index inside the database
   * 
   * @param indexName
   *          the name of the index to be checked
   * @return an object , describing the index, or null, if index does not exists
   */
  public void getIndexInfo(String indexName, IMapper mapper, Handler<AsyncResult<Object>> handler);
}
