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

/**
 * Defines an option for an IObserver, which is interpreted by the implementation
 * 
 * @author Michael Remme
 * 
 */
public @interface ObserverOption {

  /**
   * Defines the key of the option
   * 
   * @return
   */
  String key();

  /**
   * Defines the value of the option
   * 
   * @return
   */
  String value();

}
