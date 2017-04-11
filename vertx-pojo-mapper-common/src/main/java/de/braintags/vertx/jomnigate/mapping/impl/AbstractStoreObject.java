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
package de.braintags.vertx.jomnigate.mapping.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * An abstract implementation of IStoreObject
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractStoreObject<T, F> implements IStoreObject<T, F> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractStoreObject.class);

  private IMapper<T> mapper;
  private T entity = null;
  private Collection<IObjectReference> objectReferences = new ArrayList<>();
  private boolean newInstance = true;
  protected F container;

  public AbstractStoreObject(IMapper<T> mapper, T entity, F container) {
    if (mapper == null)
      throw new NullPointerException("Mapper must not be null");
    if (entity == null)
      throw new NullPointerException("Entity must not be null");
    this.mapper = mapper;
    this.entity = entity;
    this.container = container;
  }

  public AbstractStoreObject(F container, IMapper<T> mapper) {
    if (mapper == null)
      throw new NullPointerException("Mapper must not be null");
    if (container == null)
      throw new NullPointerException("Container must not be null");
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
  public final IMapper<T> getMapper() {
    return mapper;
  }

  @Override
  public final T getEntity() {
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
  public final F getContainer() {
    return container;
  }

  /**
   * Initialize the internal entity from the information previously read from the datastore.
   * 
   * @param handler
   */
  public void initToEntity(Handler<AsyncResult<Void>> handler) {
    T tmpObject = getMapper().getObjectFactory().createInstance(getMapper().getMapperClass());
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
  }

  protected void finishToEntity(T tmpObject, Handler<AsyncResult<Void>> handler) {
    this.entity = tmpObject;
    getMapper().executeLifecycle(AfterLoad.class, entity, handler);
  }

  @SuppressWarnings("rawtypes")
  protected final void iterateFields(T tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateFields");
    Set<String> fieldNames = getMapper().getFieldNames();
    List<Future> fl = new ArrayList<>(fieldNames.size());
    for (String fieldName : fieldNames) {
      Future<Void> f = Future.future();
      fl.add(f);
      IProperty field = getMapper().getField(fieldName);
      LOGGER.debug("handling field " + field.getFullName());
      field.getPropertyMapper().fromStoreObject(tmpObject, this, field, f.completer());
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  protected void iterateObjectReferences(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateObjectReferences");
    if (getObjectReferences().isEmpty()) {
      LOGGER.debug("nothing to do");
      handler.handle(Future.succeededFuture());
      return;
    }
    Collection<IObjectReference> refs = getObjectReferences();
    List<Future> fl = new ArrayList<>(refs.size());
    for (IObjectReference ref : refs) {
      LOGGER.debug("handling object reference " + ref.getField().getFullName());
      Future<Void> f = Future.future();
      fl.add(f);
      ref.getField().getPropertyMapper().fromObjectReference(tmpObject, ref, f.completer());
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Initialize the internal entity into the StoreObject
   * 
   * @param handler
   */
  @SuppressWarnings("rawtypes")
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    List<Future> fl = new ArrayList<>();
    for (String fieldName : mapper.getFieldNames()) {
      fl.add(initFieldFromEntity(fieldName));
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future initFieldFromEntity(String fieldName) {
    Future f = Future.future();
    IProperty field = mapper.getField(fieldName);
    field.getPropertyMapper().intoStoreObject(entity, this, field, f.completer());
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mapper.getTableInfo().getName() + ": " + container;
  }

}
