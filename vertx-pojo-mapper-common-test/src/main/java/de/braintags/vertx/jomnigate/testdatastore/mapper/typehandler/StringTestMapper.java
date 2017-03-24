/*-
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
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import java.util.ArrayList;
import java.util.Collection;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;

/**
 * Testing tpyehandler for Strings
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class StringTestMapper extends BaseRecord {
  public int counter;
  public String stringField = "myString";

  @Embedded
  public Collection<BooleanMapper> booleanMapperList = new ArrayList<>();

  public StringTestMapper() {
  }

  public StringTestMapper(int counter) {
    this.counter = counter;
    stringField += " " + counter;
    booleanMapperList.add(new BooleanMapper());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return counter;
  }

  @Override
  public boolean equals(Object ob) {
    if (!getClass().equals(ob.getClass()))
      return false;
    return ((StringTestMapper) ob).counter == counter;
  }

}
