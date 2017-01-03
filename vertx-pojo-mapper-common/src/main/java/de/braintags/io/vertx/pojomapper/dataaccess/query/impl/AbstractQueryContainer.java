/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * An abstract implementation of {@link IQueryContainer}
 * 
 * @author Michael Remme
 * @param <T>
 *          defines the class of the parent instance, which is an instance of IQueryContainer
 */

public abstract class AbstractQueryContainer implements IQueryContainer {

  private List<IQueryPart> queryParts;

  public AbstractQueryContainer(IQueryPart... queryParts) {
    if (queryParts != null)
      this.queryParts = Arrays.asList(queryParts);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getContent()
   */
  @Override
  public List<IQueryPart> getContent() {
    return queryParts;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IRamblerSource#applyTo(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQueryRambler, io.vertx.core.Handler)
   */
  @Override
  public void applyTo(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.apply(this, result -> {
      if (result.failed())
        resultHandler.handle(Future.failedFuture(result.cause()));
      else
        resultHandler.handle(Future.succeededFuture());
    });
  }

}
