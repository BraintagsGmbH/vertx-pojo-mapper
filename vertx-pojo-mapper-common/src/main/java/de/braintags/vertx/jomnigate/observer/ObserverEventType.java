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
package de.braintags.vertx.jomnigate.observer;

/**
 * Defines the possible event types which can be executed by the observer system
 * 
 * @author Michael Remme
 * 
 */
public enum ObserverEventType {

  /**
   * Event type which is executed directly before the mapping of a mapper class is executed
   */
  BEFORE_MAPPING,
  /**
   * Event type which is executed directly after the mapping of a mapper class was executed
   */
  AFTER_MAPPING,
  /**
   * Event type which is executed directly after an instance was deleted from the datastore
   */
  AFTER_DELETE,
  /**
   * Event typw which is executed after an instance was loaded from the datastore
   */
  AFTER_LOAD,
  /**
   * Event type which is executed after an instance was inserted or updated in the datastore
   */
  AFTER_SAVE,
  /**
   * Event type which is executed directly before an instance is deleted from the datastore
   */
  BEFORE_DELETE,
  /**
   * Event type which is executed directly before an instance is loaded from the datastore
   */
  BEFORE_LOAD,
  /**
   * Event type which is executed directly before an instance is inserted or updated in the datastore
   */
  BEFORE_SAVE;

}
