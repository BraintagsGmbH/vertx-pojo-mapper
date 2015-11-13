/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.impl.DefaultObjectFactory;

/**
 * IMapper is responsible to collect information about the mapped class
 * 
 * @author Michael Remme
 * 
 */

public interface IMapper {

  /**
   * Get information about the table / collection inside the connected datastore
   * 
   * @return the name
   */
  public ITableInfo getTableInfo();

  /**
   * Get the underlaying class which defines the mapper
   * 
   * @return the class
   */
  public Class<?> getMapperClass();

  /**
   * Get the {@link IObjectFactory} which is defined for the current mapper. To define the {@link IObjectFactory} for
   * the class, you will use the annotation {@link DefaultObjectFactory}
   * 
   * @return the {@link IObjectFactory} to be used
   */
  IObjectFactory getObjectFactory();

  /**
   * Get a list of fieldnames which are handled by the current mapper
   * 
   * @return the mapped fieldnames
   */
  public Set<String> getFieldNames();

  /**
   * Get the {@link IField} as a descriptor for the given field name
   * 
   * @param name
   *          the name of the field
   * @return an instance of {@link IField} or null, if field does not exist
   */
  public IField getField(String name);

  /**
   * Get the {@link IField} which is defined to be the id
   * 
   * @return the id field
   */
  public IField getIdField();

  /**
   * Get the methods of the mapper which are annotated by the given lifecycle annotation like {@link BeforeLoad},
   * {@link BeforeSave}, {@link BeforeDelete}, {@link AfterLoad}, {@link AfterSave}, {@link AfterDelete}
   * 
   * @param annotation
   *          an annotation like defined above
   * @return a list of annotated methods or null, if no method was annotated
   */
  public List<Method> getLifecycleMethods(Class<? extends Annotation> annotation);

  /**
   * Get the definition of {@link Entity}, if it was defined for the current mapper
   * 
   * @return the defined {@link Entity} or null
   */
  public Entity getEntity();

  /**
   * Get a defined {@link Annotation} of the given class
   * 
   * @param annotationClass
   *          the annotation class where we are interested in
   * @return a defined annotation or null
   */
  public Annotation getAnnotation(Class<? extends Annotation> annotationClass);

  /**
   * Get all {@link IField} of the current mapper, which are annotated with the specified class
   * 
   * @param annotationClass
   *          the annotation class where we are interested in
   * @return found fields or empty array, if none is annotated with the given annotation class
   */
  public IField[] getAnnotatedFields(Class<? extends Annotation> annotationClass);

  /**
   * Execute those lifecycle methods which are annotated by the given lifecycle annotation like {@link BeforeLoad},
   * {@link BeforeSave}, {@link BeforeDelete}, {@link AfterLoad}, {@link AfterSave}, {@link AfterDelete}
   * 
   * @param annotationClass
   *          execute all methods, which are annotated by this annotation
   * @param entity
   *          the entity to be handled
   */
  public void executeLifecycle(Class<? extends Annotation> annotationClass, Object entity);

  /**
   * Get the parent {@link IMapperFactory}
   * 
   * @return the factory
   */
  public IMapperFactory getMapperFactory();

  /**
   * If true, then the mapper should be synchronized with the underlaying {@link IDataStore}
   * 
   * @return the syncNeeded
   */
  public boolean isSyncNeeded();

  /**
   * If true, then the mapper should be synchronized with the underlaying {@link IDataStore}
   * 
   * @param syncNeeded
   *          the syncNeeded to set
   */
  public void setSyncNeeded(boolean syncNeeded);

  /**
   * This property defines, wether referenced objects inside a mapper are stored direct and recursive or wether they are
   * read from the store by using an {@link IObjectReference}
   * 
   * @return true, if referenced objects shall be stored recursive and false, if an {@link IObjectReference} shall be
   *         used
   */
  public boolean handleReferencedRecursive();
}
