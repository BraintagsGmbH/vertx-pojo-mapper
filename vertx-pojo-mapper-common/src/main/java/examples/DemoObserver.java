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
package examples;

import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import examples.mapper.SimpleMapper;
import io.vertx.core.Future;

public class DemoObserver extends AbstractObserver {

  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) { // <2>
    ((SimpleMapper) event.getSource()).number = context.get("counter", 1);
    context.put("counter", ((SimpleMapper) event.getSource()).number + 1);
    System.out.println("counter raised");
    return Future.succeededFuture();
  }

}
