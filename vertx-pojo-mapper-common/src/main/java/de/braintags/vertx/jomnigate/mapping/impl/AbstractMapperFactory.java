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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.util.ResultObject;
import de.braintags.vertx.util.exception.InitException;
import io.vertx.core.Future;

/**
 * An abstract implementation of IMapperFactory
 * 
 * @author Michael Remme
 */
public abstract class AbstractMapperFactory implements IMapperFactory {
  private IDataStore<?, ?> datastore;
  private Map<String, IMapper<?>> mappedClasses = new HashMap<>();

  /**
   * @param dataStore
   */
  public AbstractMapperFactory(IDataStore<?, ?> dataStore) {
    this.datastore = dataStore;
  }

  @Override
  public void reset() {
    mappedClasses = new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getDataStore()
   */
  @Override
  public final IDataStore<?, ?> getDataStore() {
    return datastore;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <T> IMapper<T> getMapper(Class<T> mapperClass) {
    String className = mapperClass.getName();
    if (mappedClasses.containsKey(className)) {
      return (IMapper<T>) mappedClasses.get(className);
    }
    if (!mapperClass.isAnnotationPresent(Entity.class))
      throw new UnsupportedOperationException(String
          .format("The class %s is no mappable entity. Add the annotation Entity to the class", mapperClass.getName()));

    IMapper<T> mapper = createMapperBlocking(mapperClass);
    Map<String, IMapper<?>> tmpMap = new HashMap<>(mappedClasses);
    tmpMap.put(className, mapper);
    mappedClasses = tmpMap;
    return mapper;
  }

  private final <T> IMapper<T> createMapperBlocking(Class<T> mapperClass) {
    IObserverContext context = IObserverContext.createInstance();
    preMapping(mapperClass, context);
    IMapper<T> mapper = createMapper(mapperClass);
    postMapping(mapper, context);
    return mapper;
  }

  private void preMapping(Class<?> mapperClass, IObserverContext context) {
    CountDownLatch latch = new CountDownLatch(1);
    getDataStore().getVertx().executeBlocking(future -> {
      getDataStore().getSettings().getObserverSettings();
    }, false, res -> {

    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new InitException("Init of mapping not possible", e);
    }
  }

  private void postMapping(IMapper<?> mapper, IObserverContext context) {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<Void> ro = new ResultObject<>(null);
    getDataStore().getVertx().<Void> executeBlocking(future -> {
      Future<Void> mf = mapper.getObserverHandler().handleAfterMapping(mapper, context);
      if (mf.failed()) {
        future.fail(mf.cause());
      } else {
        future.complete();
      }
    }, false, res -> {
      if (res.failed()) {
        ro.setThrowable(res.cause());
      } else {
        // its void
      }
      latch.countDown();
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new InitException("Init of mapping not possible", e);
    }
    if (ro.isError()) {
      throw new InitException(ro.getThrowable());
    }
  }

  @Override
  public final boolean isMapper(Class<?> mapperClass) {
    if (mappedClasses.containsKey(mapperClass.getName()) || mapperClass.isAnnotationPresent(Entity.class))
      return true;
    return false;
  }

  /**
   * Creates a new instance of IMapper for the given class
   * 
   * @param mapperClass
   *          the class to be mapped
   * @return the mapper
   */
  protected abstract <T> IMapper<T> createMapper(Class<T> mapperClass);

}
