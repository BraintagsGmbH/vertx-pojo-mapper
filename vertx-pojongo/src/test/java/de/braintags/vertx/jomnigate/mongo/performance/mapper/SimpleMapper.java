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
package de.braintags.vertx.jomnigate.mongo.performance.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;

/**
 * Person is used as mapper class
 * 
 * @author Michael Remme
 * 
 */

@Entity
public class SimpleMapper {

  @Id
  public String idField;
  private String name;
  public String name1;

  /**
   * 
   */
  public SimpleMapper() {
  }

  /**
   * 
   */
  public SimpleMapper(int count) {
    this.name = "name " + count;
    this.name1 = "name1 " + count;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

}
