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

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class BeforeSaveObserver extends AbstractObserver {
  public static boolean executed = false;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent)
   */
  @SuppressWarnings({ "unused", "unchecked" })
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    try {
      IWrite<?> write = (IWrite<?>) event.getAccessObject();
      executed = true;
      SimpleMapper sm = (SimpleMapper) event.getSource();
      sm.intValue = context.get("counter", 1);
      context.put("counter", sm.intValue + 1);
      return Future.succeededFuture();
    } catch (Exception e) {
      return Future.failedFuture(e);
    }
  }

}
