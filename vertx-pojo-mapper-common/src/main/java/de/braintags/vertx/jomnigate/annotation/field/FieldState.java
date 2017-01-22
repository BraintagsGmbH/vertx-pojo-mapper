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
package de.braintags.vertx.jomnigate.annotation.field;

/**
 * Defines the possible states of a field
 * 
 * @author mremme
 * 
 */
public enum FieldState {

  /**
   * A field is empty ( null or empty String )
   */
  EMPTY,
  /**
   * a field has content
   */
  FILLED,
  /**
   * both states, FILLED and EMPTY
   */
  ALL;
}
