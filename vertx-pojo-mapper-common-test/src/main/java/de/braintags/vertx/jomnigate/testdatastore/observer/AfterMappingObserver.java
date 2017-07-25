/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.observer;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class AfterMappingObserver extends AbstractObserver {
  public static boolean executed = false;
  public static List<ObserverEventType> executedTypes = new ArrayList<>();

  public static void reset() {
    executed = false;
    executedTypes = new ArrayList<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    executedTypes.add(event.getEventType());
    IMapper<?> mapper = (IMapper<?>) event.getSource();
    AfterMappingObserver.executed = true;
    return Future.succeededFuture();
  }

}
