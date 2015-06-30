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

import java.lang.reflect.Field;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MappedField implements IField {
  private IPropertyAccessor accessor;
  private Field field;
  private Mapper mapper;
  private ITypeHandler typeHandler;

  /**
   * 
   */
  public MappedField(Field field, IPropertyAccessor accessor, Mapper mapper) {
    this.accessor = accessor;
    this.field = field;
    this.mapper = mapper;
    init();
  }

  private void init() {
    typeHandler = mapper.getMapperFactory().getDataStore().getTypeHandlerFactory().getTypeHandler(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getPropertyDescriptor()
   */
  @Override
  public IPropertyAccessor getPropertyAccessor() {
    return accessor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getTypeHandler()
   */
  @Override
  public ITypeHandler getTypeHandler() {
    return typeHandler;
  }
}
