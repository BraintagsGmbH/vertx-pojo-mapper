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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Observer;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.IObserverHandler;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverHandler implements IObserverHandler {
  private List<ObserverSettings<?>> observerList = new ArrayList<>();
  private Map<ObserverEventType, List<IObserver>> eventObserverCache = new HashMap<>();
  private IMapper<?> mapper;

  /**
   * 
   */
  public DefaultObserverHandler(IMapper<?> mapper) {
    this.mapper = mapper;
    computeObserver();
  }

  /**
   * Computes the list of all observers, which can be executed for the current mapper class.
   */
  private void computeObserver() {
    List<ObserverSettings<?>> tmpList = new ArrayList<>();
    List<ObserverSettings<?>> osl = mapper.getMapperFactory().getDataStore().getSettings().getObserverSettings();
    osl.stream().filter(os -> os.isApplyableFor(mapper)).forEach(c -> tmpList.add(c));
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
      observerList.stream().filter(os -> os.isApplyableFor(event)).forEach(os -> {
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

  /**
   * This method executes the event on all {@link IObserver} which are registered for
   * 
   * @param event
   * @param handler
   */
  Future<Void> handleEvent(IObserverEvent event) {
    return Future.failedFuture(new UnsupportedOperationException());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserverHandler#handleBeforeSave(de.braintags.vertx.jomnigate.dataaccess.
   * write.IWrite)
   */
  @Override
  public Future<Void> handleBeforeSave(IWrite<?> writeObject) {
    Future<Void> f = Future.future();
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_SAVE);
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      CompositeFuture cf = loopBeforeSave(ol, writeObject);
      cf.setHandler(cfr -> {
        if (cfr.failed()) {
          f.fail(cfr.cause());
        } else {
          f.complete();
        }
      });
    }
    return f;
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture loopBeforeSave(List<IObserver> ol, IWrite<?> writeObject) {
    List<Future> fl = new ArrayList<>();
    for (IObserver observer : ol) {
      fl.add(handleBeforSave_Observer(observer, writeObject));
    }
    return CompositeFuture.all(fl);
  }

  @SuppressWarnings("rawtypes")
  private Future<Void> handleBeforSave_Observer(IObserver observer, IWrite<?> writeObject) {
    Future<Void> f = Future.future();
    List<Future> fl = new ArrayList<>();
    Iterator<?> selection = writeObject.getSelection();
    while (selection.hasNext()) {
      fl.add(observer
          .handleEvent(IObserverEvent.createEvent(ObserverEventType.BEFORE_SAVE, selection.next(), null, writeObject)));
    }

    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        f.complete();
      }
    });
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
  public Future<Void> handleAfterSave(IWrite<?> writeObject, IWriteResult writeResult) {
    Future<Void> f = Future.future();
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_SAVE);
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      f.fail("unimplemented");
    }
    return f;
  }
}
