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

package de.braintags.io.vertx.pojomapper.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Describes a field of an {@link IMapper}
 * 
 * @author Michael Remme
 * 
 */

public interface IField {

  /**
   * Get the simple name of the field, like defined in the mapper class
   * 
   * @return the name
   */
  public String getName();

  /**
   * Get the full name of the field
   * 
   * @return the classname.fieldname
   */
  public String getFullName();

  /**
   * Get the name of the field in the {@link IDataStore}
   * 
   * @return
   */
  public String getMappedFieldName();

  /**
   * Get the fitting {@link IPropertyAccessor} for the current field
   * 
   * @return
   */
  public IPropertyAccessor getPropertyAccessor();

  /**
   * Get the fitting {@link ITypeHandler} which is responsible to change data into and from the propriate format
   * 
   * @return
   */
  public ITypeHandler getTypeHandler();

  /**
   * Get the fitting {@link IPropertyMapper} for the current field.
   * 
   * @return an instance of {@link IPropertyMapper}, {@link IEmbeddedMapper} or {@link IReferencedMapper}
   */
  public IPropertyMapper getPropertyMapper();

  /**
   * Get a defined {@link Annotation} of the given class
   * 
   * @param annotationClass
   *          a defined annotation or null
   * @return
   */
  public Annotation getAnnotation(Class<? extends Annotation> annotationClass);

  /**
   * Is the annotation defined?
   * 
   * @param annotationClass
   *          the annotation to be examined
   * @return true, if annotatin is defined
   */
  public boolean hasAnnotation(Class<? extends Annotation> annotationClass);

  /**
   * Get the parent instance of {@link IMapper}
   * 
   * @return the mapper
   */
  public IMapper getMapper();

  /**
   * Get the constructor with the given parameters
   * 
   * @return a constructor with the arguments or null
   */
  public Constructor<?> getConstructor(Class<?>... parameters);

  /**
   * Get the underlaying {@link Field}
   * 
   * @return the field
   */
  public Field getField();

  /**
   * Is this field a {@link Set}?
   * 
   * @return true, if field is an instance of {@link Set}
   */
  public boolean isSet();

  /**
   * Is this field a {@link Map}?
   * 
   * @return true, if field is an instance of {@link Map}
   */
  public boolean isMap();

  /**
   * If the underlying java type is a map then it returns T from Map<T,V>
   * 
   * @return the CLass of the key, if this is a {@link Map}
   */
  public Class<?> getMapKeyClass();

  /**
   * returns the type of the underlying java field
   * 
   * @return the type class
   */
  public Class<?> getType();

  /**
   * If the java field is a list/array/map then the sub-type T is returned (ex. List<T>, T[], Map<?,T>
   * 
   * @return the sub class
   */
  public Class<?> getSubClass();

  /**
   * If the java field is a list/array/map and the type of the members can be examined, then here the propriate
   * ITypeHandler is returned
   * 
   * @return the {@link ITypeHandler} to deal with the subtype, or NULL if subtype can't be read
   */
  public ITypeHandler getSubTypeHandler();

  /**
   * If the java field is a list / array / map the the sub type is returned
   * 
   * @return the subtype
   */
  public Type getSubType();

  /**
   * Is the field defining a single value?
   * 
   * @return true, if single value
   */
  public boolean isSingleValue();

  /**
   * Get the list of defined type parameters of the current field
   * 
   * @return the list of type parameters
   */
  public List<IField> getTypeParameters();

  /**
   * Get the information whether the field defines an array
   * 
   * @return the isArray
   */
  public boolean isArray();

  /**
   * Get the information whether the field defines a {@link Collection}
   * 
   * @return the isCollection
   */
  public boolean isCollection();

}
