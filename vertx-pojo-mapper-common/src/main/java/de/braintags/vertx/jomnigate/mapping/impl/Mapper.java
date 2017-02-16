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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.KeyGenerator;
import de.braintags.vertx.jomnigate.annotation.ObjectFactory;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableGenerator;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.util.ClassUtil;
import de.braintags.vertx.util.exception.ClassAccessException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * This implementation of {@link IMapper} is using the bean convention to define fields, which shall be mapped. It is
 * first reading all public, non transient fields, then the bean-methods ( public getter/setter ). The way of mapping
 * can be defined by adding several annotations to the field
 *
 * @author Michael Remme
 *
 */

public class Mapper<T> implements IMapper<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(Mapper.class);

  private IObjectFactory objectFactory;
  private Map<String, MappedField> mappedFields = new HashMap<>();
  private IField idField;
  private MapperFactory mapperFactory;
  private Class<T> mapperClass;
  private Entity entity;
  private Map<Class<? extends Annotation>, IField[]> fieldCache = new HashMap<>();
  private ITableInfo tableInfo;
  private boolean syncNeeded = true;
  private IKeyGenerator keyGenerator;

  /**
   * all annotations which shall be examined for the mapper class itself
   */
  private static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS = Arrays.asList(Indexes.class,
      KeyGenerator.class);

  /**
   * all annotations which shall be examined for the mapper class itself
   */
  private static final List<Class<? extends Annotation>> LIFECYCLE_ANNOTATIONS = Arrays.asList(AfterDelete.class,
      AfterLoad.class, AfterSave.class, BeforeDelete.class, BeforeLoad.class, BeforeSave.class);

  /**
   * Class annotations which were found inside the current definition
   */
  private final Map<Class<? extends Annotation>, Annotation> existingClassAnnotations = new HashMap<>();

  /**
   * Methods which are life-cycle events. Per event there can be several methods defined
   */
  private final Map<Class<? extends Annotation>, List<IMethodProxy>> lifecycleMethods = new HashMap<>();

  private Indexes indexes;

  /**
   * Creates a new definition for the given mapper class
   *
   * @param mapperClass
   *          the mapper class to be handled
   * @param mapperFactory
   *          the parent {@link MapperFactory}
   */
  public Mapper(Class<T> mapperClass, MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    this.mapperClass = mapperClass;
    this.objectFactory = new DefaultObjectFactory();
    this.objectFactory.setMapper(this);
    init();
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

  private void init() {
    computePersistentFields();
    computeLifeCycleAnnotations();
    computeClassAnnotations();
    computeEntity();
    computeIndize();
    computeObjectFactory();
    computeKeyGenerator();
    generateTableInfo();
    validate();
  }

  private void computeIndize() {
    if (mapperClass.isAnnotationPresent(Indexes.class)) {
      indexes = mapperClass.getAnnotation(Indexes.class);
    }

  }

  private void computeKeyGenerator() {
    if (getMapperFactory().getDataStore() != null) {
      KeyGenerator gen = (KeyGenerator) getAnnotation(KeyGenerator.class);
      if (gen != null) {
        String name = gen.value();
        keyGenerator = getMapperFactory().getDataStore().getKeyGenerator(name);
      } else {
        keyGenerator = getMapperFactory().getDataStore().getDefaultKeyGenerator();
      }
    }
  }

  private void generateTableInfo() {
    if (mapperFactory.getDataStore() != null) {
      ITableGenerator tg = mapperFactory.getDataStore().getTableGenerator();
      this.tableInfo = tg.createTableInfo(this);
      for (String fn : getFieldNames()) {
        IField field = getField(fn);
        IColumnHandler ch = tg.getColumnHandler(field);
        this.tableInfo.createColumnInfo(field, ch);
      }
    }
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
    }
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
    List<IMethodProxy> lcMethods = lifecycleMethods.get(ann);
    if (lcMethods == null) {
      lcMethods = new ArrayList<>();
      lifecycleMethods.put(ann, lcMethods);
    }

    MethodProxy mp = new MethodProxy(method, this);
    if (!lcMethods.contains(mp)) {
      lcMethods.add(mp);
    }
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
        Field field = ClassUtil.getDeclaredField(mapperClass, name);
        if (field == null) {
          throw new NoSuchFieldException("Field not found: " + name);
        }
        addMappedField(name, createMappedField(field, accessor));
      }
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
      JavaFieldAccessor accessor = new JavaFieldAccessor(field);
      MappedField mf = createMappedField(field, accessor);
      if (!mf.isIgnore() && !Modifier.isTransient(fieldModifiers)
          && (Modifier.isPublic(fieldModifiers) && !Modifier.isStatic(fieldModifiers))) {
        addMappedField(accessor.getName(), createMappedField(field, accessor));
      }
    }
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
   * Adds a mapped field into the list of properties
   * 
   * @param name
   * @param mf
   */
  protected void addMappedField(String name, MappedField mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (idField != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      idField = mf;
    }
    if (!mf.isIgnore()) {
      mappedFields.put(name, mf);
    }
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
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getFieldNames()
   */
  @Override
  public Set<String> getFieldNames() {
    return mappedFields.keySet();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getField(java.lang.String)
   */
  @Override
  public IField getField(String name) {
    IField field = mappedFields.get(name);
    if (field == null)
      throw new de.braintags.vertx.jomnigate.exception.NoSuchFieldException(this, name);
    return field;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getMapperClass()
   */
  @Override
  public Class<T> getMapperClass() {
    return mapperClass;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getLifecycleMethods(java.lang.Class)
   */
  @Override
  public List<IMethodProxy> getLifecycleMethods(Class<? extends Annotation> annotation) {
    return lifecycleMethods.get(annotation);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getEntity()
   */
  @Override
  public Entity getEntity() {
    return this.entity;
  }

  @Override
  public Indexes getIndexDefinitions() {
    return indexes;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getAnnotation(java.lang.Class)
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
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getAnnotatedFields(java.lang.Class)
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
    return fieldCache.get(annotationClass);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#executeLifecycle(java.lang.Class, java.lang.Object)
   */
  @Override
  public void executeLifecycle(Class<? extends Annotation> annotationClass, T entity,
      Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start executing Lifecycle " + annotationClass.getSimpleName());
    List<IMethodProxy> methods = getLifecycleMethods(annotationClass);
    if (methods == null || methods.isEmpty()) {
      LOGGER.debug("nothing to execute");
      handler.handle(Future.succeededFuture());
    } else {
      executeLifecycleMethods(entity, handler, methods);
    }
  }

  /**
   * @param entity
   * @param handler
   * @param methods
   */
  private void executeLifecycleMethods(Object entity, Handler<AsyncResult<Void>> handler, List<IMethodProxy> methods) {
    CompositeFuture cf = CompositeFuture.all(createFutureList(entity, methods));
    cf.setHandler(res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private List<Future> createFutureList(Object entity, List<IMethodProxy> methods) {
    List<Future> fl = new ArrayList<>();
    for (IMethodProxy mp : methods) {
      Future f = Future.future();
      LOGGER.debug("execute lifecycle method: " + getMapperClass().getSimpleName() + " - " + mp.getMethod().getName());
      executeMethod(mp, entity, f.completer());
      fl.add(f);
    }
    return fl;
  }

  /**
   * Execute the trigger method. IMPORTANT: if a TriggerContext is created, the handler is informed by the
   * TriggerContext, if not, then the handler is informed by this method
   * 
   * @param mp
   * @param entity
   * @param handler
   */
  private void executeMethod(IMethodProxy mp, Object entity, Handler<AsyncResult<Void>> handler) {
    Method method = mp.getMethod();
    method.setAccessible(true);
    Object[] args = mp.getParameterTypes() == null ? null
        : new Object[] {
            getMapperFactory().getDataStore().getTriggerContextFactory().createTriggerContext(this, handler) };
    try {
      LOGGER.debug("invoking trigger method " + getMapperClass().getSimpleName() + " - " + method.getName());
      method.invoke(entity, args);
      if (args == null) {
        // ONLY INFORM HANDLER, if no TriggerContext is given
        handler.handle(Future.succeededFuture());
      }
      LOGGER.debug("trigger method invokement finished");
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public ITableInfo getTableInfo() {
    return tableInfo;
  }

  @Override
  public IField getIdField() {
    return idField;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#isSyncNeeded()
   */
  @Override
  public final boolean isSyncNeeded() {
    return syncNeeded;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#setSyncNeeded(boolean)
   */
  @Override
  public final void setSyncNeeded(boolean syncNeeded) {
    this.syncNeeded = syncNeeded;
  }

  @Override
  public boolean handleReferencedRecursive() {
    return this.getMapperFactory().getDataStore().getProperties().getBoolean(IDataStore.HANDLE_REFERENCED_RECURSIVE,
        false);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getKeyGenerator()
   */
  @Override
  public IKeyGenerator getKeyGenerator() {
    return keyGenerator;
  }
}
