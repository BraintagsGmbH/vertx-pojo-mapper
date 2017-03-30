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
package de.braintags.vertx.jomnigate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;

/**
 * The annotation defines an {@link IObserver}, which shall be applicated to the entities of a mapper.
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Observer {

  /**
   * Defines the observer class, which shall be used
   * 
   * @return the class of the observer
   */
  Class<? extends IObserver> observerClass();

  /**
   * Defines the priority of the given definition. Larger is higher priority.
   * 
   * @return the priority, default is 1
   */
  int priority() default 1;

  /**
   * Defines the event types, where for the observer shall be executed. Default is empty list, which means, that the
   * observer is executed on any event for the annotated mapper
   * 
   * @return defined event types, default is an empty list
   */
  ObserverEventType[] eventTypes() default {};
}
