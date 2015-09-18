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

package de.braintags.io.vertx.pojomapper;

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
}
