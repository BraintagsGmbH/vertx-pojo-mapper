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

import java.util.Iterator;

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
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
public class BeforeSaveObserver implements IObserver {
  public static boolean executed = false;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    IWrite<?> write = (IWrite<?>) event.getAccessObject();
    executed = true;
    Iterator<?> it = write.getSelection();
    while (it.hasNext()) {
      SimpleMapper sm = (SimpleMapper) it.next();
      sm.intValue = context.get("counter", 1);
      context.put("counter", sm.intValue + 1);
      executed = true;
    }

    return Future.succeededFuture();
  }

  @Override
  public boolean handlesEvent(IObserverEvent event, IObserverContext context) {
    return true;
  }

}
