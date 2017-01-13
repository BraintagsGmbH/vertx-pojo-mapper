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

package de.braintags.io.vertx.pojomapper.dataaccess.query;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * The result of an executed {@link IQuery#executeCount(io.vertx.core.Handler)}
 * 
 * @author Michael Remme
 * 
 */

public interface IQueryCountResult {

  /**
   * Get the {@link IDataStore} by which the current instance was created
   * 
   * @return
   */
  public IDataStore getDataStore();

  /**
   * Get the underlaying {@link IMapper}
   * 
   * @return
   */
  public IMapper getMapper();

  /**
   * Get the original query, which was executed in the datastore
   * 
   * @return the query
   */
  public IQueryExpression getOriginalQuery();

  /**
   * Get the result of the query
   * 
   * @return the number of records which are fitting the query
   */
  public long getCount();

}
