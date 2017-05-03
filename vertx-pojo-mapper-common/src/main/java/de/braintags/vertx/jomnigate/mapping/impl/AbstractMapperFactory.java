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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverHandler;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.observer.impl.handler.BeforeMappingHandler;
import de.braintags.vertx.util.ResultObject;
import de.braintags.vertx.util.exception.InitException;
import io.vertx.core.Future;

/**
 * An abstract implementation of IMapperFactory
 * 
 * @author Michael Remme
 */
public abstract class AbstractMapperFactory implements IMapperFactory {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractMapperFactory.class);

  private IDataStore<?, ?> datastore;
  private Map<String, IMapper<?>> mappedClasses = new HashMap<>();
  private BeforeMappingHandler beforeMappingHandler = new BeforeMappingHandler();
  private Object so = new Object();

  /**
   * @param dataStore
   */
  public AbstractMapperFactory(IDataStore<?, ?> dataStore) {
    this.datastore = dataStore;
  }

  @Override
  public void reset() {
    synchronized (so) {
      mappedClasses = new HashMap<>();
    }
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

    synchronized (so) {
      Map<String, IMapper<?>> tmpMap = new HashMap<>(mappedClasses);
      tmpMap.put(className, mapper);
      mappedClasses = tmpMap;
    }
    return mapper;
  }

  private final <T> IMapper<T> createMapperBlocking(Class<T> mapperClass) {
    IObserverContext context = IObserverContext.createInstance();
    LOGGER.debug("pre mapping for " + mapperClass.getName());
    preMapping(mapperClass, context);
    LOGGER.debug("createMapper for " + mapperClass.getName());
    IMapper<T> mapper = createMapper(mapperClass);
    LOGGER.debug("post mapping for " + mapperClass.getName());
    postMapping(mapper, context);
    return mapper;
  }

  private void preMapping(Class<?> mapperClass, IObserverContext context) {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<Void> ro = new ResultObject<>(null);

    // using only vertx.executeBlocking will potentially block with MongoDatastore
    // thus - during init - we are using Runnable
    Runnable runnable = () -> {
      try {
        LOGGER.debug("start handle before mapping");
        Future<Void> f = handleBeforeMapping(mapperClass, context);
        LOGGER.debug("stop handle before mapping");
        if (f.failed()) {
          ro.setThrowable(f.cause());
        } else {
          // no result to be set;
        }
      } catch (Exception e) {
        ro.setThrowable(e);
      }
      latch.countDown();
    };
    Thread thr = new Thread(runnable);
    thr.start();

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new InitException("Init of mapping not possible", e);
    }
    if (ro.isError()) {
      throw new InitException(ro.getThrowable());
    }
  }

  /**
   * Performs the event {@link ObserverEventType#BEFORE_MAPPING} for the given mapper class.
   * This method is contained inside the current instance and not inside the {@link IObserverHandler}, cause the
   * IObserverhandler is part of the IMapper, which will be created later
   * 
   * @param mapperClass
   * @param context
   * @return
   */
  private <T> Future<Void> handleBeforeMapping(Class<T> mapperClass, IObserverContext context) {
    List<IObserver> ol = getObserver(mapperClass, ObserverEventType.BEFORE_MAPPING);
    Future<Void> f = null;
    if (ol.isEmpty()) {
      f = Future.succeededFuture();
    } else {
      f = getBeforeMappingHandler().handle(mapperClass, context, ol, this.getDataStore());
    }
    return f;
  }

  /**
   * Get the observer, which are responsible for the event BEFORE_MAPPING for the given class
   * 
   * @param mapperClass
   * @return
   */
  private List<IObserver> getObserver(final Class<?> mapperClass, final ObserverEventType eventType) {
    List<ObserverDefinition<?>> osList = getDataStore().getSettings().getObserverSettings()
        .getObserverDefinitions(mapperClass, eventType);
    List<IObserver> ol = new ArrayList<>();
    osList.forEach(os -> {
      try {
        ol.add(os.getObserverClass().newInstance());
      } catch (Exception e) {
        throw new MappingException(e);
      }
    });
    return ol;
  }

  private void postMapping(IMapper<?> mapper, IObserverContext context) {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<Void> ro = new ResultObject<>(null);

    // using only vertx.executeBlocking will potentially block with MongoDatastore
    // thus - during init - we are using Runnable
    Runnable runnable = () -> {
      try {
        LOGGER.debug("start handle after mapping");
        Future<Void> f = mapper.getObserverHandler().handleAfterMapping(mapper, context);
        LOGGER.debug("stop handle after mapping");
        if (f.failed()) {
          ro.setThrowable(f.cause());
        } else {
          // no result to be set;
        }
      } catch (Exception e) {
        ro.setThrowable(e);
      }
      latch.countDown();
    };

    Thread thr = new Thread(runnable);
    thr.start();

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

  /**
   * @return the beforeMappingHandler
   */
  protected BeforeMappingHandler getBeforeMappingHandler() {
    return beforeMappingHandler;
  }

}
