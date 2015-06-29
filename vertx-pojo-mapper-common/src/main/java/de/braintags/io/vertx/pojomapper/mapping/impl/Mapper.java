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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.exception.ClassAccessException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class Mapper implements IMapper {
  private IObjectFactory objectFactory = new ObjectFactory();
  private Map<String, MappedField> mappedFields = new HashMap<String, MappedField>();
  private MapperFactory mapperFactory;
  private Class<?> mapperClass;

  /**
   * 
   */
  public Mapper(Class<?> mapperClass, MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    this.mapperClass = mapperClass;
    init();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getObjectFactory()
   */
  @Override
  public IObjectFactory getObjectFactory() {
    return objectFactory;
  }

  private void init() {
    computePersistentFields();
  }

  private void computePersistentFields() {
    computeFieldProperties();
    computeBeanProperties();
  }

  /**
   * Computes the properties in JavaBean format
   */
  public void computeBeanProperties() {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(mapperClass);
      PropertyDescriptor[] beanDescriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < beanDescriptors.length; i++) {
        Method readMethod = beanDescriptors[i].getReadMethod();
        Method writeMethod = beanDescriptors[i].getWriteMethod();
        if (readMethod != null && writeMethod != null) {
          JavaBeanAccessor accessor = new JavaBeanAccessor(beanDescriptors[i]);
          String name = accessor.getName();
          Field field = mapperClass.getDeclaredField(name);
          mappedFields.put(name, new MappedField(field, accessor, this));
        }
      }
    } catch (IntrospectionException | NoSuchFieldException e) {
      throw new ClassAccessException("Cannot perform introspection of class", e);
    }
  }

  /**
   * Computes the properties from the public fields of the class, which are not transient
   */
  public void computeFieldProperties() {
    Field[] fieldArray = mapperClass.getFields();
    for (int i = 0; i < fieldArray.length; i++) {
      Field field = fieldArray[i];
      int fieldModifiers = field.getModifiers();
      if (!Modifier.isTransient(fieldModifiers)
          && (Modifier.isPublic(fieldModifiers) && !Modifier.isStatic(fieldModifiers))) {
        JavaFieldAccessor accessor = new JavaFieldAccessor(field);
        mappedFields.put(accessor.getName(), new MappedField(field, accessor, this));
      }
    }
  }

  /**
   * Get the {@link MapperFactory} which created the current instance
   * 
   * @return
   */
  MapperFactory getMapperFactory() {
    return mapperFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getFieldNames()
   */
  @Override
  public Set<String> getFieldNames() {
    return mappedFields.keySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getField(java.lang.String)
   */
  @Override
  public IField getField(String name) {
    return mappedFields.get(name);
  }
}
