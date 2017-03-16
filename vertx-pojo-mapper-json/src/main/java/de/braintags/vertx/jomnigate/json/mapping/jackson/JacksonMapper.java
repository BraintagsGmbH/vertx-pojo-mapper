/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.mapping.jackson;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class JacksonMapper<T> implements IMapper<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JacksonMapper.class);

  private JacksonMapperFactory mapperFactory;
  private BeanDescription beanDescription;
  private Class<T> mapperClass;

  public JacksonMapper(Class<T> mapperClass, JacksonMapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    this.mapperClass = mapperClass;
    init();
  }

  private void init() {
    LOGGER.debug("examining " + mapperClass.getName());
    ObjectMapper mapper = ((JsonDatastore) mapperFactory.getDataStore()).getJacksonMapper();
    JavaType type = mapper.constructType(mapperClass);
    this.beanDescription = mapper.getSerializationConfig().introspect(type);

    computeLifeCycleAnnotations();
    computeClassAnnotations();
    computeEntity();
    computeIndize();
    computeObjectFactory();
    computeKeyGenerator();
    generateTableInfo();
    checkReferencedFields();
    validate();
  }

  /**
   * Get the underlaying instance of {@link BeanDescription}, which was created for the mapper class
   * 
   * @return
   */
  public BeanDescription getBeanDescription() {
    return beanDescription;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getTableInfo()
   */
  @Override
  public ITableInfo getTableInfo() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getMapperClass()
   */
  @Override
  public Class<T> getMapperClass() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getObjectFactory()
   */
  @Override
  public IObjectFactory getObjectFactory() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getFieldNames()
   */
  @Override
  public Set<String> getFieldNames() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getField(java.lang.String)
   */
  @Override
  public IProperty getField(String name) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getIdField()
   */
  @Override
  public IProperty getIdField() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getLifecycleMethods(java.lang.Class)
   */
  @Override
  public List<IMethodProxy> getLifecycleMethods(Class<? extends Annotation> annotation) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getEntity()
   */
  @Override
  public Entity getEntity() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getIndexDefinitions()
   */
  @Override
  public Indexes getIndexDefinitions() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getAnnotation(java.lang.Class)
   */
  @Override
  public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getAnnotatedFields(java.lang.Class)
   */
  @Override
  public IProperty[] getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#executeLifecycle(java.lang.Class, java.lang.Object,
   * io.vertx.core.Handler)
   */
  @Override
  public void executeLifecycle(Class<? extends Annotation> annotationClass, T entity,
      Handler<AsyncResult<Void>> handler) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getMapperFactory()
   */
  @Override
  public IMapperFactory getMapperFactory() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#isSyncNeeded()
   */
  @Override
  public boolean isSyncNeeded() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#setSyncNeeded(boolean)
   */
  @Override
  public void setSyncNeeded(boolean syncNeeded) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#handleReferencedRecursive()
   */
  @Override
  public boolean handleReferencedRecursive() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getKeyGenerator()
   */
  @Override
  public IKeyGenerator getKeyGenerator() {
    return null;
  }

}
