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
import java.util.Iterator;
import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#BEFORE_SAVE }
 * 
 * @author Michael Remme
 * 
 */
public class BeforeSaveHandler extends AbstractEventHandler<IWrite<?>, IWriteResult> {

  /**
   * @param observer
   * @param writeObject
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IWrite<?> writeObject, IWriteResult result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    Iterator<?> selection = writeObject.getSelection();
    while (selection.hasNext()) {
      IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.BEFORE_SAVE, selection.next(), null,
          writeObject, writeObject.getDataStore());
      if (observer.handlesEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return fl;
  }

}
