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
package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.test.mapper.SimpleMapper;

/**
 * A simple mapper with some beans properties
 *
 * @author Michael Remme
 * 
 */

public class SimpleMapper {
  @Id
  public String id;
  public String name;
  private String secondProperty;
  public int intValue;
  public String beforeSave;
  public String afterSave;
  public String afterLoad;

  /**
   * 
   */
  public SimpleMapper() {
  }

  /**
   * 
   */
  public SimpleMapper(String name, String secondProperty) {
    this.name = name;
    this.secondProperty = secondProperty;
  }

  /**
   * @return the secondProperty
   */
  public final String getSecondProperty() {
    return secondProperty;
  }

  /**
   * @param secondProperty
   *          the secondProperty to set
   */
  public final void setSecondProperty(String secondProperty) {
    this.secondProperty = secondProperty;
  }

  @Override
  public boolean equals(Object o) {
    SimpleMapper compare = (SimpleMapper) o;

    return o != null && compare.id.equals(id) && compare.name.equals(name)
        && compare.secondProperty.equals(secondProperty);
  }

  @Override
  public String toString() {
    return String.valueOf(name);
  }

  @BeforeSave
  public void BeforeSave() {
    beforeSave = "succeeded";
  }

  @AfterSave
  public void afterSaveMethod() {
    afterSave = "succeeded";
  }

  @AfterLoad
  public void afterLoadMethod() {
    afterLoad = "succeeded";
  }

}
