/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Mapper with all properties to test {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */

public class TypehandlerTestMapper {
  @Id
  public String id;
  public String stringField = "myString";
  public int myInt = 5;

  /**
   * 
   */
  public TypehandlerTestMapper() {
  }

  @Override
  public boolean equals(Object ob) {
    TypehandlerTestMapper compare = (TypehandlerTestMapper) ob;
    if (!compare.stringField.equals(stringField))
      return false;
    if (compare.myInt != myInt)
      return false;

    return true;
  }

}
