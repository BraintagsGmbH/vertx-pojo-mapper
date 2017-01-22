/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler;

/**
 * Handles byte and creates TINYINT from it Properties for scale and precision are not used
 * 
 * @author Michael Remme
 * 
 */

public class ByteColumnHandler extends NumericColumnHandler {
  public static final String TINYINT_TYPE = "TINYINT";

  /**
   * Constructor for a ByteColumnHandler
   */
  public ByteColumnHandler() {
    super(TINYINT_TYPE, false, false, Byte.class, byte.class);
  }

}
