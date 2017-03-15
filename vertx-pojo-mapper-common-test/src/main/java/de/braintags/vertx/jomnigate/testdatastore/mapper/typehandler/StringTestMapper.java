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

import de.braintags.vertx.jomnigate.annotation.Entity;

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

  public StringTestMapper() {
  }

  public StringTestMapper(int counter) {
    this.counter = counter;
    stringField += " " + counter;
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
