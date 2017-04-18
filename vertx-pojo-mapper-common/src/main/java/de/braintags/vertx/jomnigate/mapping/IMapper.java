/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapping;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.KeyGenerator;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mapping.impl.DefaultObjectFactory;
import de.braintags.vertx.jomnigate.observer.IObserverHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * IMapper is responsible to collect information about the mapped class
 * 
 * @author Michael Remme
 * @param <T>
 *          the class of the underlaying mapper
 */

public interface IMapper<T> {

  /**
   * Get information about the table / collection inside the connected datastore
   * 
   * @return the name
   */
  ITableInfo getTableInfo();

  /**
   * Get the underlaying class which defines the mapper
   * 
   * @return the class
   */
  Class<T> getMapperClass();

  /**
   * Returns true if at least one field of the mapper is annotated with {@link Referenced}
   * 
   * @return
   */
  boolean hasReferencedFields();

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
  Set<String> getFieldNames();

  /**
   * Get the {@link IProperty} as a descriptor for the given field name
   * 
   * @param name
   *          the name of the field
   * @return an instance of {@link IProperty} or null, if field does not exist
   */
  IProperty getField(String name);

  /**
   * Get the {@link IProperty} which is defined to be the id
   * 
   * @return the id field
   */
  IMappedIdField getIdField();

  /**
   * Get the methods of the mapper which are annotated by the given lifecycle annotation like {@link BeforeLoad},
   * {@link BeforeSave}, {@link BeforeDelete}, {@link AfterLoad}, {@link AfterSave}, {@link AfterDelete}
   * 
   * @param annotation
   *          an annotation like defined above
   * @return a list of annotated methods or null, if no method was annotated
   */
  List<IMethodProxy> getLifecycleMethods(Class<? extends Annotation> annotation);

  /**
   * Get the definition of {@link Entity}, if it was defined for the current mapper
   * 
   * @return the defined {@link Entity} or null
   */
  Entity getEntity();

  /**
   * Get the definitions about indexes, which shall be created for the current mapper
   * 
   * @return the index definitions
   */
  Indexes getIndexDefinitions();

  /**
   * Get a defined {@link Annotation} of the given class
   * 
   * @param annotationClass
   *          the annotation class where we are interested in
   * @return a defined annotation or null
   */
  <U extends Annotation> U getAnnotation(Class<U> annotationClass);

  /**
   * Get all {@link IProperty} of the current mapper, which are annotated with the specified class
   * 
   * @param annotationClass
   *          the annotation class where we are interested in
   * @return found fields or empty array, if none is annotated with the given annotation class
   */
  IProperty[] getAnnotatedFields(Class<? extends Annotation> annotationClass);

  /**
   * Execute those lifecycle methods which are annotated by the given lifecycle annotation like {@link BeforeLoad},
   * {@link BeforeSave}, {@link BeforeDelete}, {@link AfterLoad}, {@link AfterSave}, {@link AfterDelete}
   * 
   * @param annotationClass
   *          execute all methods, which are annotated by this annotation
   * @param entity
   *          the entity to be handled
   */
  void executeLifecycle(Class<? extends Annotation> annotationClass, T entity, Handler<AsyncResult<Void>> handler);

  /**
   * Get the parent {@link IMapperFactory}
   * 
   * @return the factory
   */
  IMapperFactory getMapperFactory();

  /**
   * If true, then the mapper should be synchronized with the underlaying {@link IDataStore}
   * 
   * @return the syncNeeded
   */
  boolean isSyncNeeded();

  /**
   * If true, then the mapper should be synchronized with the underlaying {@link IDataStore}
   * 
   * @param syncNeeded
   *          the syncNeeded to set
   */
  void setSyncNeeded(boolean syncNeeded);

  /**
   * This property defines, wether referenced objects inside a mapper are stored direct and recursive or wether they are
   * read from the store by using an {@link IObjectReference}
   * 
   * @return true, if referenced objects shall be stored recursive and false, if an {@link IObjectReference} shall be
   *         used
   */
  boolean handleReferencedRecursive();

  /**
   * Retrive the {@link IKeyGenerator} for the current mapper. This method reacts to the annotation
   * {@link KeyGenerator}. If none is set, then the method {@link IDataStore#getDefaultKeyGenerator()} is requested
   * 
   * @return an instance of {@link IKeyGenerator} or null, if none defined or supported by {@link IDataStore}
   */
  IKeyGenerator getKeyGenerator();

  /**
   * Get the reference, which is used to access a new key from a defined {@link IKeyGenerator}
   * 
   * @return
   */
  String getKeyGeneratorReference();

  /**
   * Get the instance of {@link IObserverHandler} for the current mapper
   * 
   * @return
   */
  IObserverHandler getObserverHandler();
}
