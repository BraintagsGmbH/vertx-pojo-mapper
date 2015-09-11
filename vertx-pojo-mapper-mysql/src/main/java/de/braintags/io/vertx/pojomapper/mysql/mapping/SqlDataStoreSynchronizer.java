/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.mapping;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlDataStoreSynchronizer implements IDataStoreSynchronizer {
  private MySqlDataStore datastore;

  /**
   * 
   */
  public SqlDataStoreSynchronizer(MySqlDataStore ds) {
    this.datastore = ds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer#synchronize(de.braintags.io.vertx.pojomapper.mapping
   * .IMapper, io.vertx.core.Handler)
   */
  @Override
  public void synchronize(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    throw new UnsupportedOperationException();
  }

}
