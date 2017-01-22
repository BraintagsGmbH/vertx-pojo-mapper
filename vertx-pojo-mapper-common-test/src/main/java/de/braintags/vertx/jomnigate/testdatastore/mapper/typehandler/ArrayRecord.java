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
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import de.braintags.vertx.jomnigate.annotation.Entity;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class ArrayRecord extends BaseRecord {
  public String[] array = new String[3];
  public String[] arrayWithEqualValues = new String[3];
  public String[] arrayWithNullValues = new String[3];

  public ArrayRecord() {
    array[0] = "eins";
    array[1] = "zwei";
    array[2] = "drei";

    arrayWithEqualValues[0] = "eins";
    arrayWithEqualValues[1] = "eins";
    arrayWithEqualValues[2] = "eins";

    arrayWithNullValues[2] = "eins";
  }

}
