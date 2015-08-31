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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.Indexes;
import de.braintags.io.vertx.pojomapper.annotation.ObjectFactory;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.exception.ClassAccessException;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.exception.MethodAccessException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.util.ClassUtil;

/**
 * This implementation of {@link IMapper} is using the bean convention to define fields, which shall be mapped. It is
 * first reading all public, non transient fields, then the bean-methods ( public getter/setter ). The way of mapping
 * can be defined by adding several annotations to the field
 * 
 * @author Michael Remme
 * 
 */

public class Mapper implements IMapper {
  private IObjectFactory objectFactory;
  private Map<String, MappedField> mappedFields = new HashMap<String, MappedField>();
  private IField idField;
  private MapperFactory mapperFactory;
  private Class<?> mapperClass;
  private Entity entity;
  private Map<Class<? extends Annotation>, IField[]> fieldCache = new HashMap<Class<? extends Annotation>, IField[]>();
  private String dataStoreName;

  /**
   * all annotations which shall be examined for the mapper class itself
   */
  private static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS = Arrays.asList(Indexes.class);

  /**
   * all annotations which shall be examined for the mapper class itself
   */
  private static final List<Class<? extends Annotation>> LIFECYCLE_ANNOTATIONS = Arrays.asList(AfterDelete.class,
      AfterLoad.class, AfterSave.class, BeforeDelete.class, BeforeLoad.class, BeforeSave.class);

  /**
   * Class annotations which were found inside the current definition
   */
  private final Map<Class<? extends Annotation>, Annotation> existingClassAnnotations = new HashMap<Class<? extends Annotation>, Annotation>();

  /**
   * Methods which are life-cycle events. Per event there can be several methods defined
   */
  private final Map<Class<? extends Annotation>, List<Method>> lifecycleMethods = new HashMap<Class<? extends Annotation>, List<Method>>();

  /**
   * @throws Exception
   * 
   */
  public Mapper(Class<?> mapperClass, MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    this.mapperClass = mapperClass;
    this.objectFactory = new DefaultObjectFactory();
    this.objectFactory.setMapper(this);
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
    computeLifeCycleAnnotations();
    computeClassAnnotations();
    computeEntity();
    computeObjectFactory();
    validate();
  }

  /**
   * Validation for required properties etc
   */
  private void validate() {
    if (idField == null)
      throw new MappingException("No id-field specified in mapper " + getMapperClass().getName());
  }

  private void computeEntity() {
    if (mapperClass.isAnnotationPresent(Entity.class)) {
      entity = mapperClass.getAnnotation(Entity.class);
      dataStoreName = entity.name();
    } else
      dataStoreName = mapperClass.getSimpleName();
  }

  private void computeObjectFactory() {
    if (mapperClass.isAnnotationPresent(ObjectFactory.class)) {
      ObjectFactory ofAnn = mapperClass.getAnnotation(ObjectFactory.class);
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

  private void computeClassAnnotations() {
    for (Class<? extends Annotation> annClass : CLASS_ANNOTATIONS) {
      Annotation ann = mapperClass.getAnnotation(annClass);
      if (ann != null)
        existingClassAnnotations.put(annClass, ann);
    }
  }

  /**
   * Computes the methods, which are annotated with the lifecycle annotations like {@link BeforeLoad}
   */
  private void computeLifeCycleAnnotations() {
    List<Method> methods = ClassUtil.getDeclaredAndInheritedMethods(mapperClass);
    for (Method method : methods) {
      for (Class<? extends Annotation> ann : LIFECYCLE_ANNOTATIONS) {
        if (method.isAnnotationPresent(ann)) {
          addLifecycleAnnotationMethod(ann, method);
        }
      }
    }
  }

  private void addLifecycleAnnotationMethod(Class<? extends Annotation> ann, Method method) {
    List<Method> lcMethods = lifecycleMethods.get(ann);
    if (lcMethods == null) {
      lcMethods = new ArrayList<Method>();
      lifecycleMethods.put(ann, lcMethods);
    }
    if (!lcMethods.contains(method))
      lcMethods.add(method);
  }

  /**
   * Compute all fields, which shall be persisted. First the public, non-transient fields are read, then the
   * bean-methods.
   */
  private void computePersistentFields() {
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
      BeanInfo beanInfo = Introspector.getBeanInfo(mapperClass);
      PropertyDescriptor[] beanDescriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < beanDescriptors.length; i++) {
        Method readMethod = beanDescriptors[i].getReadMethod();
        Method writeMethod = beanDescriptors[i].getWriteMethod();
        if (readMethod != null && writeMethod != null) {
          JavaBeanAccessor accessor = new JavaBeanAccessor(beanDescriptors[i]);
          String name = accessor.getName();
          Field field = mapperClass.getDeclaredField(name);
          addMappedField(name, createMappedField(field, accessor));
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
        addMappedField(accessor.getName(), createMappedField(field, accessor));
      }
    }
  }

  protected MappedField createMappedField(Field field, IPropertyAccessor accessor) {
    return new MappedField(field, accessor, this);
  }

  private void addMappedField(String name, MappedField mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (idField != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      idField = mf;
    }
    mappedFields.put(name, mf);
  }

  /**
   * Get the {@link MapperFactory} which created the current instance
   * 
   * @return
   */
  @Override
  public MapperFactory getMapperFactory() {
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
    IField field = mappedFields.get(name);
    if (field == null)
      throw new MappingException(String.format("Field '%s' does not exist in %s", name, getMapperClass().getName()));
    return field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getMapperClass()
   */
  @Override
  public Class<?> getMapperClass() {
    return mapperClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getLifecycleMethods(java.lang.Class)
   */
  @Override
  public List<Method> getLifecycleMethods(Class<? extends Annotation> annotation) {
    return lifecycleMethods.get(annotation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getEntity()
   */
  @Override
  public Entity getEntity() {
    return this.entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getAnnotation(java.lang.Class)
   */
  @Override
  public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
    return existingClassAnnotations.get(annotationClass);
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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#getAnnotatedFields(java.lang.Class)
   */
  @Override
  public IField[] getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    if (!fieldCache.containsKey(annotationClass)) {
      IField[] result = new IField[0];
      for (MappedField field : mappedFields.values()) {
        if (field.getAnnotation(annotationClass) != null) {
          IField[] newArray = new IField[result.length + 1];
          System.arraycopy(result, 0, newArray, 0, result.length);
          result = newArray;
          result[result.length - 1] = field;
        }
      }
      fieldCache.put(annotationClass, result);
    }

    IField[] result = fieldCache.get(annotationClass);
    if (result.length == 0)
      return null;
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapper#executeLifecycle(java.lang.Class, java.lang.Object)
   */
  @Override
  public void executeLifecycle(Class<? extends Annotation> annotationClass, Object entity) {
    List<Method> methods = getLifecycleMethods(annotationClass);
    if (methods != null) {
      for (Method method : methods) {
        method.setAccessible(true);
        Object[] args = null;
        if (method.getParameterCount() > 0) {
          args = createMethodArgs(method, entity);
        }

        try {
          Object result = method.invoke(entity, args);
          if (result != null)
            throw new UnsupportedOperationException("Not yet supported, return value of annotated lifecyle methods: "
                + result.getClass().getName());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new MethodAccessException(e);
        }
      }
    }
  }

  /**
   * Lets try to generate some arguments like {@link IDataStore}, {@link IStoreObject} for instance?
   * 
   * @param method
   * @param entity
   * @return
   */
  private Object[] createMethodArgs(Method method, Object entity) {
    throw new UnsupportedOperationException("Not yet supported, dynamic generation of arguments");
  }

  @Override
  public String getDataStoreName() {
    return dataStoreName;
  }

  @Override
  public IField getIdField() {
    return idField;
  }
}
