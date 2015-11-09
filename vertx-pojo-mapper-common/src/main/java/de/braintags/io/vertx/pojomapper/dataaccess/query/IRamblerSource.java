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
package de.braintags.io.vertx.pojomapper.dataaccess.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * All elements of a query, which contain this interface will be called to fill a {@link IQueryRambler}
 * 
 * @author Michael Remme
 * 
 */

public interface IRamblerSource {

  /**
   * apply the current instance to the {@link IQueryRambler}
   * 
   * @param rambler
   */
  void applyTo(IQueryRambler ramblerHandler, Handler<AsyncResult<Void>> resultHandler);
}
