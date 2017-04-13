/*
* #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * #L%
*/
package de.braintags.vertx.jomnigate.mapping.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.ObjectFactory;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.util.ClassUtil;
import de.braintags.vertx.util.exception.ClassAccessException;

/**
 * This implementation of {@link IMapper} is using the bean convention to define fields, which shall be mapped. It is
 * first reading all public, non transient fields, then the bean-methods ( public getter/setter ). The way of mapping
 * can be defined by adding several annotations to the field
 *
 * @author Michael Remme
 * @param <T>
 *          the class of the underlaying mapper
 */

public class Mapper<T> extends AbstractMapper<T> {
  private IObjectFactory objectFactory;
  private String keyGeneratorReference;

  /**
   * Creates a new definition for the given mapper class
   *
   * @param getMapperClass()
   *          the mapper class to be handled
   * @param mapperFactory
   *          the parent {@link MapperFactory}
   */
  public Mapper(Class<T> mapperClass, MapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    this.objectFactory = new DefaultObjectFactory();
    this.objectFactory.setMapper(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getObjectFactory()
   */
  @Override
  public IObjectFactory getObjectFactory() {
    return objectFactory;
  }

  @Override
  protected void init() {
    super.init();
    this.keyGeneratorReference = getEntity().polyClass() == Object.class ? getMapperClass().getSimpleName()
        : getEntity().polyClass().getSimpleName();
    computeObjectFactory();
  }

  private void computeObjectFactory() {
    if (getMapperClass().isAnnotationPresent(ObjectFactory.class)) {
      ObjectFactory ofAnn = getMapperClass().getAnnotation(ObjectFactory.class);
      String className = ofAnn.className();
      try {
        Class<?> ofClass = Class.forName(className);
        IObjectFactory of = (IObjectFactory) ofClass.newInstance();
        this.objectFactory = of;
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        throw new MappingException("Problems in mapping process", e);
      }
    }
  }

  /**
   * Compute all fields, which shall be persisted. First the public, non-transient fields are read, then the
   * bean-methods.
   */
  @Override
  protected void computePersistentFields() {
    computeFieldProperties();
    computeBeanProperties();
  }

  /**
   * Computes the properties in JavaBean format. Important: the bean-methods are defining the property to be used and
   * the methods are used to write and read information from an instance. Annotations for further definition of the
   * mapping are read from the underlaying field
   */
  public void computeBeanProperties() {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(getMapperClass());
      PropertyDescriptor[] beanDescriptors = beanInfo.getPropertyDescriptors();
      loopPropertyDescriptors(beanDescriptors);
    } catch (IntrospectionException | NoSuchFieldException e) {
      throw new ClassAccessException("Cannot perform introspection of class", e);
    }
  }

  /**
   * @param beanDescriptors
   * @throws NoSuchFieldException
   */
  private void loopPropertyDescriptors(PropertyDescriptor[] beanDescriptors) throws NoSuchFieldException {
    for (int i = 0; i < beanDescriptors.length; i++) {
      Method readMethod = beanDescriptors[i].getReadMethod();
      Method writeMethod = beanDescriptors[i].getWriteMethod();
      if (readMethod != null && writeMethod != null) {
        JavaBeanAccessor accessor = new JavaBeanAccessor(beanDescriptors[i]);
        String name = accessor.getName();
        Field field = ClassUtil.getDeclaredField(getMapperClass(), name);
        if (field != null) {
          addMappedField(name, createMappedField(field, accessor));
        }
      }
    }
  }

  /**
   * Computes the properties from the public fields of the class, which are not transient
   */
  public void computeFieldProperties() {
    Field[] fieldArray = getMapperClass().getFields();
    for (int i = 0; i < fieldArray.length; i++) {
      Field field = fieldArray[i];
      int fieldModifiers = field.getModifiers();
      JavaFieldAccessor accessor = new JavaFieldAccessor(field);
      MappedField mf = createMappedField(field, accessor);
      if (!mf.isIgnore() && !Modifier.isTransient(fieldModifiers)
          && (Modifier.isPublic(fieldModifiers) && !Modifier.isStatic(fieldModifiers))) {
        addMappedField(accessor.getName(), createMappedField(field, accessor));
      }
    }
  }

  /**
   * Adds a mapped field into the list of properties
   * 
   * @param name
   * @param mf
   */
  protected void addMappedField(String name, MappedField mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (getIdField() != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      setIdField(createIdField(mf));
    }
    if (!mf.isIgnore()) {
      this.getMappedProperties().put(name, mf);
    }
  }

  /**
   * Create the id field for this mapper
   * 
   * @param mappedField
   *          the field with the {@link Id} annotation
   * @return an id field
   */
  protected IMappedIdField createIdField(MappedField mappedField) {
    return new MappedIdFieldImpl(mappedField);
  }

  /**
   * Create a new instance of {@link MappedField}
   * 
   * @param field
   * @param accessor
   * @return
   */
  protected MappedField createMappedField(Field field, IPropertyAccessor accessor) {
    return new MappedField(field, accessor, this);
  }

  /**
   * Add an Annotation, for which the Mapper shall be checked. Existing annotations of that type can be requested by
   * method {@link #getAnnotation(Class)}
   *
   * @param annotation
   *          the Annotation class, which we are interested in
   */
  public static void addInterestingAnnotation(final Class<? extends Annotation> annotation) {
    CLASS_ANNOTATIONS.add(annotation);
  }

  @Override
  public boolean handleReferencedRecursive() {
    return this.getMapperFactory().getDataStore().getProperties().getBoolean(IDataStore.HANDLE_REFERENCED_RECURSIVE,
        false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getKeyGeneratorReference()
   */
  @Override
  public String getKeyGeneratorReference() {
    return keyGeneratorReference;
  }
}
