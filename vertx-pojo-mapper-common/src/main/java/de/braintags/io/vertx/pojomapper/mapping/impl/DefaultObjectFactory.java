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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectFactory;

/**
 * Default implementation of {@link IObjectFactory}
 * 
 * @author Michael Remme
 * 
 */

public class DefaultObjectFactory implements IObjectFactory {
  private IMapper mapper;
  private Class<?> DEFAULT_LIST_CLASS = ArrayList.class;
  private Class<?> DEFAULT_SET_CLASS = HashSet.class;

  /**
   * 
   */
  public DefaultObjectFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IObjectFactory#createInstance(java.lang.Class)
   */
  @Override
  public <T> T createInstance(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new MappingException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IObjectFactory#setMapper(de.braintags.io.vertx.pojomapper.mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IObjectFactory#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  @Override
  public Collection<?> createCollection(IField field) {
    if (field.isSet()) {
      return createSet(field);
    } else
      return createList(field);
  }

  private Set<?> createSet(IField field) {
    if (field.getConstructor() != null)
      return newInstance(field.getConstructor(), DEFAULT_SET_CLASS);
  }

  private List<?> createList(IField field) {
    throw new UnsupportedOperationException();
  }

  /**
   * creates an instance of testType (if it isn't Object.class or null) or fallbackType
   */
  private <T> T newInstance(final Constructor<T> tryMe, final Class<T> fallbackType) {
    if (tryMe != null) {
      tryMe.setAccessible(true);
      try {
        return tryMe.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return createInst(fallbackType);
  }

}
