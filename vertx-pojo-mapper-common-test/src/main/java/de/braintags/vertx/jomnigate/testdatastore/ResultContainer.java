/*-
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.testdatastore;

import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryCountResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class ResultContainer {
  // public AssertionError assertionError;
  public IWriteResult writeResult;
  public IQueryResult<?> queryResult;
  public IQueryCountResult queryResultCount;
  public IDeleteResult deleteResult;

  /**
   * 
   */
  public ResultContainer() {
  }

}
