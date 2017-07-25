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

import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestObserver4 extends AbstractObserver {

  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    return Future.succeededFuture();
  }

}
