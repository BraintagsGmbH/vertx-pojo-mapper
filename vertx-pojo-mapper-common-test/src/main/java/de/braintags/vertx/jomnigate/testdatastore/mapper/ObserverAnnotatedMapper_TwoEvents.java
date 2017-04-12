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
package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.annotation.Observer;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Observer(observerClass = TestObserver.class, eventTypes = { ObserverEventType.AFTER_DELETE,
    ObserverEventType.BEFORE_DELETE })
public class ObserverAnnotatedMapper_TwoEvents extends BaseRecord {

  /**
   * 
   */
  public ObserverAnnotatedMapper_TwoEvents() {
  }

}
