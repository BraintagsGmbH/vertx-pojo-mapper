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

import java.util.Date;

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
  public Integer myInteger = new Integer(14);
  public float myFloat = 5.88f;
  public Float myFloatOb = new Float(12.5);
  public boolean myBo = true;
  public Boolean myBoolean = new Boolean(false);
  public Date javaDate = new Date(System.currentTimeMillis());

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
    if (compare.myFloat != myFloat)
      return false;
    if (!compare.myInteger.equals(myInteger))
      return false;
    if (!compare.myFloatOb.equals(myFloatOb))
      return false;
    if (compare.myBo != myBo)
      return false;
    if (!compare.myBoolean.equals(myBoolean))
      return false;
    if (!compare.javaDate.equals(javaDate))
      return false;

    return true;
  }

}
