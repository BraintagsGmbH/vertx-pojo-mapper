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

import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SimpleMapperObserver implements IObserver {
  public static boolean executed = false;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    ((SimpleMapper) event.getSource()).intValue = context.get("counter", 1);
    context.put("counter", ((SimpleMapper) event.getSource()).intValue + 1);
    executed = true;
    return Future.succeededFuture();
  }

  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return event.getSource() instanceof SimpleMapper;
  }

}
