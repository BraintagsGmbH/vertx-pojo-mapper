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

/**
 * Extensions of {@link IStreamResult} can be added to a {@link QueryReadStream} before start of processing to get some
 * further information about the execution
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of entity, which is processed by the current instance
 */
public interface IStreamResult<T> {

  /**
   * Called after an entity was processed succesfull. This method may raise a counter or add some further information
   * about the processed entity
   * 
   * @param entity
   */
  void succeededEntity(T entity);

  /**
   * Called, when an entity was processed and processing failed. Can be used to count the number of failed instances and
   * to log the exception for instance
   * 
   * @param entity
   * @param e
   */
  void failedEntity(T entity, Throwable e);

  /**
   * Get the number of succeeded instances
   * 
   * @return
   */
  int getSucceeded();

  /**
   * Get the number of failed instances
   * 
   * @return
   */
  int getFailed();

}
