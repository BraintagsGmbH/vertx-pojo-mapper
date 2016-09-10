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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * An abstract implementation of IStoreObject
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractStoreObject<T> implements IStoreObject<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractStoreObject.class);

  private IMapper mapper;
  private Object entity = null;
  private Collection<IObjectReference> objectReferences = new ArrayList<>();
  private boolean newInstance = true;
  protected T container;

  public AbstractStoreObject(IMapper mapper, Object entity, T container) {
    if (mapper == null)
      throw new NullPointerException("Mapper must not be null");
    this.mapper = mapper;
    this.entity = entity;
    this.container = container;
  }

  public AbstractStoreObject(T container, IMapper mapper) {
    if (mapper == null)
      throw new NullPointerException("Mapper must not be null");
    this.mapper = mapper;
    this.container = container;
  }

  /**
   * @return the newInstance
   */
  @Override
  public final boolean isNewInstance() {
    return newInstance;
  }

  /**
   * @param newInstance
   *          the newInstance to set
   */
  public final void setNewInstance(boolean newInstance) {
    this.newInstance = newInstance;
  }

  /**
   * @return the mapper
   */
  public final IMapper getMapper() {
    return mapper;
  }

  @Override
  public final Object getEntity() {
    if (entity == null) {
      String message = String.format("Internal Entity is not initialized; call method %s.initToEntity first ",
          getClass().getName());
      throw new NullPointerException(message);
    }
    return entity;
  }

  @Override
  public final Collection<IObjectReference> getObjectReferences() {
    return objectReferences;
  }

  @Override
  public final T getContainer() {
    return container;
  }

  /**
   * Initialize the internal entity from the information previously read from the datastore.
   * 
   * @param handler
   */
  public final void initToEntity(Handler<AsyncResult<Void>> handler) {
    try {
      Object tmpObject = getMapper().getObjectFactory().createInstance(getMapper().getMapperClass());
      LOGGER.debug("start initToEntity");
      iterateFields(tmpObject, fieldResult -> {
        if (fieldResult.failed()) {
          handler.handle(fieldResult);
          return;
        }
        iterateObjectReferences(tmpObject, orResult -> {
          if (orResult.failed()) {
            handler.handle(orResult);
            return;
          }
          finishToEntity(tmpObject, handler);
          LOGGER.debug("finished initToEntity");
        });
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  protected void finishToEntity(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    this.entity = tmpObject;
    getMapper().executeLifecycle(AfterLoad.class, entity, handler);
  }

  protected final void iterateFields(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateFields");
    Set<String> fieldNames = getMapper().getFieldNames();
    CounterObject<Void> co = new CounterObject<>(fieldNames.size(), handler);
    for (String fieldName : fieldNames) {
      IField field = getMapper().getField(fieldName);
      LOGGER.debug("handling field " + field.getFullName());
      field.getPropertyMapper().fromStoreObject(tmpObject, this, field, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        }
        if (co.reduce()) {
          LOGGER.debug("field counter finished");
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  protected void iterateObjectReferences(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateObjectReferences");
    if (getObjectReferences().isEmpty()) {
      LOGGER.debug("nothing to do");
      handler.handle(Future.succeededFuture());
      return;
    }
    Collection<IObjectReference> refs = getObjectReferences();
    CounterObject<Void> co = new CounterObject<>(refs.size(), handler);
    for (IObjectReference ref : refs) {
      LOGGER.debug("handling object reference " + ref.getField().getFullName());
      ref.getField().getPropertyMapper().fromObjectReference(tmpObject, ref, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        }
        if (co.reduce()) {
          LOGGER.debug("object references finished");
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  /**
   * Initialize the internal entity into the StoreObject
   * 
   * @param handler
   */
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    CounterObject<Void> co = new CounterObject<>(mapper.getFieldNames().size(), handler);
    for (String fieldName : mapper.getFieldNames()) {
      initFieldFromEntity(fieldName, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
        } else {
          if (co.reduce())
            handler.handle(Future.succeededFuture());
        }
      });
      if (co.isError()) {
        break;
      }
    }
  }

  protected void initFieldFromEntity(String fieldName, Handler<AsyncResult<Void>> handler) {
    IField field = mapper.getField(fieldName);
    field.getPropertyMapper().intoStoreObject(entity, this, field, handler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mapper.getTableInfo().getName() + ": " + String.valueOf(container);
  }

}
