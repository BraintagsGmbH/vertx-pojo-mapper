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
package de.braintags.vertx.jomnigate.observer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Observer;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.delete.impl.AfterDeleteHandler;
import de.braintags.vertx.jomnigate.dataaccess.delete.impl.BeforeDeleteHandler;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.AfterSaveHandler;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.BeforeSaveHandler;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverHandler;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * Default implementation for {@link IObserverHandler}
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverHandler implements IObserverHandler {
  private List<ObserverSettings<?>> observerList = new ArrayList<>();
  private Map<ObserverEventType, List<IObserver>> eventObserverCache = new HashMap<>();
  private IMapper<?> mapper;
  private BeforeSaveHandler beforeSaveHandler = new BeforeSaveHandler();
  private AfterSaveHandler afterSaveHandler = new AfterSaveHandler();
  private AfterLoadHandler afterLoadHandler = new AfterLoadHandler();
  private BeforeLoadHandler beforeLoadHandler = new BeforeLoadHandler();
  private AfterDeleteHandler afterDeleteHandler = new AfterDeleteHandler();
  private BeforeDeleteHandler beforeDeleteHandler = new BeforeDeleteHandler();
  private AfterMappingHandler afterMappingHandler = new AfterMappingHandler();

  /**
   * Create a new instance, where usable observers are examined
   * 
   * @param mapper
   */
  public DefaultObserverHandler(IMapper<?> mapper) {
    this.mapper = mapper;
    computeObserver();
  }

  /**
   * Computes the list of all observers, which can be executed for the current mapper class.
   */
  private void computeObserver() {
    List<ObserverSettings<?>> tmpList = mapper.getMapperFactory().getDataStore().getSettings()
        .getObserverSettings(mapper);
    Observer ob = mapper.getAnnotation(Observer.class);
    if (ob != null) {
      ObserverSettings<?> os = new ObserverSettings<>(ob.observerClass());
      os.setPriority(ob.priority());
      ObserverEventType[] tl = ob.eventTypes();
      for (ObserverEventType t : tl) {
        os.getEventTypeList().add(t);
      }
      tmpList.add(os);
    }
    tmpList.sort((os1, os2) -> Integer.compare(os2.getPriority(), os1.getPriority()));
    observerList = tmpList;
  }

  @Override
  public List<IObserver> getObserver(ObserverEventType event) {
    if (!eventObserverCache.containsKey(event)) {
      List<IObserver> ol = new ArrayList<>();
      observerList.stream().filter(os -> os.isApplicableFor(event)).forEach(os -> {
        try {
          ol.add(os.getObserverClass().newInstance());
        } catch (Exception e) {
          throw new MappingException(e);
        }
      });
      eventObserverCache.put(event, ol);
    }
    return eventObserverCache.get(event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleBeforeSave(de.braintags.vertx.jomnigate.dataaccess.
   * write.IWrite)
   */
  @Override
  public <T> Future<Void> handleBeforeSave(IWrite<T> writeObject, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_SAVE);
    Future<Void> f = Future.future();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      f = getBeforeSaveHandler().handle(writeObject, null, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleAfterSave(de.braintags.vertx.jomnigate.dataaccess.
   * write.IWrite, de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult)
   */
  @Override
  public <T> Future<Void> handleAfterSave(IWrite<T> writeObject, IWriteResult writeResult, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_SAVE);
    Future<Void> f = Future.future();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      f = getAfterSaveHandler().handle(writeObject, writeResult, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleBeforeLoad(de.braintags.vertx.jomnigate.dataaccess.
   * query.IQuery, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleBeforeLoad(IQuery<T> queryObject, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_LOAD);
    Future<Void> f = Future.future();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      f = getBeforeLoadHandler().handle(queryObject, null, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleAfterLoad(de.braintags.vertx.jomnigate.dataaccess.
   * query.IQuery, de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterLoad(IQuery<T> queryObject, IQueryResult<T> queryResult,
      IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_LOAD);
    Future<Void> f = Future.future();
    if (ol.isEmpty() || queryResult.isEmpty()) {
      f.complete();
    } else {
      f = getAfterLoadHandler().handle(queryObject, queryResult, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleBeforeDelete(de.braintags.vertx.jomnigate.dataaccess.
   * delete.IDelete, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleBeforeDelete(IDelete<T> deleteObject, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_DELETE);
    Future<Void> f = Future.future();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      f = getBeforeDeleteHandler().handle(deleteObject, null, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleAfterDelete(de.braintags.vertx.jomnigate.dataaccess.
   * delete.IDelete, de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterDelete(IDelete<T> deleteObject, IDeleteResult deleteResult,
      IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_DELETE);
    Future<Void> f = Future.future();
    if (ol.isEmpty() || deleteObject.size() <= 0) {
      f.complete();
    } else {
      f = getAfterDeleteHandler().handle(deleteObject, deleteResult, context, ol);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleAfterMapping(de.braintags.vertx.jomnigate.mapping.
   * IMapper, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterMapping(IMapper<T> mapper, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_MAPPING);
    Future<Void> f = Future.future();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      f = getAfterMappingHandler().handle(mapper, context, ol);
    }
    return f;
  }

  /**
   * @return the beforeSaveHandler
   */
  protected BeforeSaveHandler getBeforeSaveHandler() {
    return beforeSaveHandler;
  }

  /**
   * @return the afterSaveHandler
   */
  protected AfterSaveHandler getAfterSaveHandler() {
    return afterSaveHandler;
  }

  /**
   * @return the afterLoadHandler
   */
  protected AfterLoadHandler getAfterLoadHandler() {
    return afterLoadHandler;
  }

  /**
   * @return the beforeLoadHandler
   */
  protected BeforeLoadHandler getBeforeLoadHandler() {
    return beforeLoadHandler;
  }

  /**
   * @return the afterDeleteHandler
   */
  protected AfterDeleteHandler getAfterDeleteHandler() {
    return afterDeleteHandler;
  }

  /**
   * @return the beforeDeleteHandler
   */
  protected BeforeDeleteHandler getBeforeDeleteHandler() {
    return beforeDeleteHandler;
  }

  /**
   * @return the afterMappingHandler
   */
  protected AfterMappingHandler getAfterMappingHandler() {
    return afterMappingHandler;
  }

}
