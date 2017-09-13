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
package de.braintags.vertx.jomnigate.dataaccess.query.stream;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default implementation counts the number of failed and succeded instances
 * 
 * @author Michael Remme
 * 
 */
public class DefaultStreamResult<T> implements IStreamResult<T> {
  private final AtomicInteger failedInstances = new AtomicInteger();
  private final AtomicInteger succeededInstances = new AtomicInteger();

  @Override
  public void succeededEntity(final T entity) {
    succeededInstances.incrementAndGet();
  }

  @Override
  public void failedEntity(final T entity, final Throwable e) {
    failedInstances.incrementAndGet();
  }

  /**
   * Get the number of succeeded instances
   * 
   * @return
   */
  @Override
  public int getSucceeded() {
    return succeededInstances.get();
  }

  /**
   * Get the number of failed instances
   * 
   * @return
   */
  @Override
  public int getFailed() {
    return failedInstances.get();
  }

}
