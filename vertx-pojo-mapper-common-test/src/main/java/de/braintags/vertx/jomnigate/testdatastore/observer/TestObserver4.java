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
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestObserver4 implements IObserver {

  /**
   * 
   */
  public TestObserver4() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserver#handlesEvent(de.braintags.vertx.jomnigate.observer.
   * ObserverEventType)
   */
  @Override
  public boolean handlesEvent(ObserverEventType event) {
    return false;
  }

  @Override
  public Future<Void> handleEvent(IObserverEvent event) {
    return Future.succeededFuture();
  }

}
