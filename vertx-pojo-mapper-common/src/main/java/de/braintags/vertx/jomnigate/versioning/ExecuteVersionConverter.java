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
package de.braintags.vertx.jomnigate.versioning;

import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import io.vertx.core.Future;

/**
 * This observer implementation is used to execute version conversion for those instances, where a {@link VersionInfo}
 * is defined. It is created and added into the observer list automatically by the mapper, when {@link VersionInfo} is
 * found.
 * 
 * @author Michael Remme
 * 
 */
public class ExecuteVersionConverter implements IObserver {

  /**
   * Creates a new instance and adds the observer into the observer system
   * 
   * @param versionInfo
   * @return
   */
  static final ExecuteVersionConverter createInstance(VersionInfo versionInfo) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserver#canHandleEvent(de.braintags.vertx.jomnigate.observer.
   * IObserverEvent, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    return Future.failedFuture(new UnsupportedOperationException());
  }

}
