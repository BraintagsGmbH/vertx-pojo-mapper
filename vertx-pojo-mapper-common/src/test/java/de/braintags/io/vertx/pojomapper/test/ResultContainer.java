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

package de.braintags.io.vertx.pojomapper.test;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class ResultContainer {
  public AssertionError assertionError;
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
