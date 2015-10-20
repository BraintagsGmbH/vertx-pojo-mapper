/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

/**
 * Handles Double / Float and creates DOUBLE from it. Properties for scale and precision are used, if set
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DoubleColumnHandler extends NumericColumnHandler {

  /**
   * Constructor for a DoubleColumnHandler
   */
  public DoubleColumnHandler() {
    super("DOUBLE", true, true, Double.class, Float.class, double.class, float.class);
  }

  /**
   * Constructor for a extending classes
   * 
   * @param classesToDeal
   *          the classes, which shall be handled
   */
  protected DoubleColumnHandler(Class<?>... classesToDeal) {
    super("DOUBLE", true, true, classesToDeal);
  }

}
