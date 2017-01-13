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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

/**
 * Handles Integer and creates INT from it Properties for scale and precision are not used
 * 
 * @author Michael Remme
 * 
 */

public class IntegerColumnHandler extends NumericColumnHandler {

  /**
   * Constructor for a IntegerColumnHandler
   */
  public IntegerColumnHandler() {
    super("INT", false, false, Integer.class, int.class);
  }

}
