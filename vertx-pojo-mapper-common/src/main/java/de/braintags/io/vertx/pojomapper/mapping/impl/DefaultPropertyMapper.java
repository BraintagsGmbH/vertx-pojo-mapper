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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.exception.TypeHandlerException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.impl.DefaultTypeHandlerResult;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultPropertyMapper implements IPropertyMapper {

  /**
   * 
   */
  public DefaultPropertyMapper() {
  }

  @Override
  public void intoStoreObject(Object mapper, IStoreObject<?> storeObject, IField field) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(mapper);
    DefaultTypeHandlerResult result = new DefaultTypeHandlerResult();

    th.intoStore(javaValue, field, result);
    result.validate();
    Object dbValue = result.getResult();
    if (javaValue != null && dbValue == null)
      throw new TypeHandlerException(String.format("Value conversion failed: original = %s, conversion = NULL",
          String.valueOf(javaValue)));
    if (dbValue != null)
      storeObject.put(field, dbValue);
  }

  @Override
  public void fromStoreObject(Object mapper, IStoreObject<?> storeObject, IField field) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object dbValue = storeObject.get(field);
    DefaultTypeHandlerResult result = new DefaultTypeHandlerResult();
    th.fromStore(dbValue, field, null, result);
    result.validate();
    Object javaValue = result.getResult();
    if (javaValue == null && dbValue != null)
      throw new TypeHandlerException(String.format("Value conversion failed: original = %s, conversion = NULL",
          String.valueOf(dbValue)));
    if (javaValue != null)
      pAcc.writeData(mapper, javaValue);
  }

}
