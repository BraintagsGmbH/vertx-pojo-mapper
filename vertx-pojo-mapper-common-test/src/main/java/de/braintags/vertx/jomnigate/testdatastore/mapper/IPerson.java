/*
 * #%L
 * vertx-pojo-mapper-json
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

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public interface IPerson {

  /**
   * @return the name
   */
  public String getName();

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name);

  public void beforeLoadFromInterface();

}
