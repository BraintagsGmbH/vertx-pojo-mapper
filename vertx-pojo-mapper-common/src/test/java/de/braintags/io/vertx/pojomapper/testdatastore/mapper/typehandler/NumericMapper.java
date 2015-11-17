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
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.braintags.io.vertx.pojomapper.annotation.Entity;

@Entity
public class NumericMapper extends BaseRecord {
  public int myInt;
  public Integer myInteger;
  public float myFloat;
  public Float myFloatOb;
  public long myLong;
  public Long myLongOb;
  public double myDoub;
  public Double myDouble;
  public short mySh;
  public Short myShort;
  public BigDecimal bigDecimal;
  public BigInteger bigInteger;
  public byte byteValue = 123;
  public Byte byteObject = 88;

}
