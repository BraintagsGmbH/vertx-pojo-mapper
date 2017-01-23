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
package de.braintags.vertx.jomnigate.exception;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;

/**
 * This exception is thrown, if an {@link IKeyGenerator} was requested by from an {@link IDataStore} which is not
 * supported
 * 
 * @author Michael Remme
 * 
 */
public class UnsupportedKeyGenerator extends RuntimeException {

  /**
   * Create a new Exception
   * 
   * @param name
   *          the requested name of the {@link IKeyGenerator}
   */
  public UnsupportedKeyGenerator(String name) {
    super(format(name));
  }

  /**
   * Create a new Exception
   * 
   * @param name
   *          the requested name of the {@link IKeyGenerator}
   * @param cause
   */
  public UnsupportedKeyGenerator(String name, Throwable cause) {
    super(format(name), cause);
  }

  private static String format(String name) {
    return String.format("The generator with the name %s is not supported by this datastore", name);
  }
}
