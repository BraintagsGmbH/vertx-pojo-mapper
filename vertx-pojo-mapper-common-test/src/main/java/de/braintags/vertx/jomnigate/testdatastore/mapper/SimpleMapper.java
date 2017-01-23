/*
 * #%L
 * vertx-pojo-mapper-common-test
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

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;

/**
 * A simple mapper with some beans properties
 *
 * @author Michael Remme
 * 
 */

@Entity
public class SimpleMapper extends BaseRecord {
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
    if (compare == null && o == null)
      return true;
    if (compare == null || o == null)
      return false;

    boolean idEqual = compareId(compare.id, id);
    return idEqual && compare.name.equals(name) && compare.secondProperty.equals(secondProperty);
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
