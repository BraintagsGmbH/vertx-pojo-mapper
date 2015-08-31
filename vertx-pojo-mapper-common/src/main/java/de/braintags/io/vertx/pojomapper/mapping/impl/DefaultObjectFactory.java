/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  private Class<?> DEFAULT_MAP_CLASS = HashMap.class;

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
    } else if (field.isCollection())
      return createList(field);
    else
      throw new UnsupportedOperationException("this should not land here");
  }

  private Set<?> createSet(IField field) {
    return (Set<?>) newInstance(field.getConstructor(), DEFAULT_SET_CLASS);
  }

  private List<?> createList(IField field) {
    return (List<?>) newInstance(field.getConstructor(), DEFAULT_LIST_CLASS);
  }

  /**
   * creates an instance of testType (if it isn't Object.class or null) or fallbackType
   */
  private Object newInstance(final Constructor<?> constructor, final Class<?> fallbackType) {
    if (constructor != null) {
      constructor.setAccessible(true);
      try {
        return constructor.newInstance();
      } catch (Exception e) {
        throw new MappingException(e);
      }
    }
    return createInstance(fallbackType);
  }

  @Override
  public Map<?, ?> createMap(IField field) {
    return (Map<?, ?>) newInstance(field.getConstructor(), DEFAULT_MAP_CLASS);
  }

}
