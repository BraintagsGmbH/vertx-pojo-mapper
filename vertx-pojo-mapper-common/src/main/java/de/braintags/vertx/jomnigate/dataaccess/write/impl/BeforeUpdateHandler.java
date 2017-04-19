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
package de.braintags.vertx.jomnigate.dataaccess.write.impl;

import de.braintags.vertx.jomnigate.observer.ObserverEventType;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class BeforeUpdateHandler extends BeforeInsertHandler {

  @Override
  protected ObserverEventType getEventType() {
    return ObserverEventType.BEFORE_UPDATE;
  }
}
