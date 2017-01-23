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
package de.braintags.vertx.jomnigate.mapping;

import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A factory which creates instances of {@link IStoreObject} which are suitable for the current {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 * @param F
 *          defines the format, which is used to save into and to read from the underlaying datastore
 */

public interface IStoreObjectFactory<F> {

  /**
   * Creates a new instance of {@link IStoreObject} by using the information from the given entity. The entity is
   * converted into the format, which can be used to save it into the {@link IDataStore}
   * 
   * @param mapper
   *          the mapper for the entity to be handled
   * @param entity
   *          the entity
   * @param handler
   *          the handler to be recalled
   */
  public <T> void createStoreObject(IMapper<T> mapper, T entity, Handler<AsyncResult<IStoreObject<T, ?>>> handler);

  /**
   * Creates a {@link List} of {@link IStoreObject} by using the informations of the given instances. All entities are
   * converted into the format, which can be used to save into the {@link IDataStore}
   * 
   * @param mapper
   *          the mapper for the entity to be handled
   * @param entities
   *          the pojos to be converted
   * @param handler
   *          the handler to be recalled
   */
  public <T> void createStoreObjects(IMapper<T> mapper, List<T> entities,
      Handler<AsyncResult<List<IStoreObject<T, ?>>>> handler);

  /**
   * Creates a new instance of {@link IStoreObject} by using the information from the given stored object from the
   * {@link IDataStore}. The informations from the storedObject are used to create a suitable POJO
   * 
   * @param storedObject
   *          the stored object from the {@link IDataStore}
   * @param mapper
   *          the mapper
   * @param handler
   *          the handler to be recalled
   */
  public <T> void createStoreObject(F storedObject, IMapper<T> mapper,
      Handler<AsyncResult<IStoreObject<T, ?>>> handler);

}
