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

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

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
    Object dbValue = th.intoStore(javaValue);
    if (dbValue != null)
      storeObject.put(field, dbValue);
  }

  @Override
  public void fromStoreObject(Object mapper, IStoreObject<?> storeObject, IField field) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object dbValue = storeObject.get(field);
    Object javaValue = th.fromStore(dbValue);
    if (javaValue != null)
      pAcc.writeData(mapper, javaValue);
  }

}
