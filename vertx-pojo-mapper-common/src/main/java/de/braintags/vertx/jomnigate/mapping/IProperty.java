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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Encoder;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.util.security.crypt.IEncoder;

/**
 * Describes a property of an {@link IMapper}
 * 
 * @author Michael Remme
 * 
 */
public interface IProperty {

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
   * Get the fitting {@link IPropertyAccessor} for the current field
   * 
   * @return
   */
  public IPropertyAccessor getPropertyAccessor();

  /**
   * Get the fitting {@link ITypeHandler} which is responsible to change data into and from the propriate format
   * 
   * @return
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
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
   * If for the current field an annotation {@link Embedded} or {@link Referenced} is defined, then it is returned here
   * 
   * @return an annotation of type {@link Embedded} or {@link Referenced} or null
   */
  public Annotation getEmbedRef();

  /**
   * Get the parent instance of {@link IMapper}
   * 
   * @return the mapper
   */
  public IMapper getMapper();

  /**
   * Get the constructor with the given parameters
   * 
   * @param parameters
   *          the parameters of the required Constructor
   * @return a constructor with the arguments or null
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
  @SuppressWarnings("rawtypes")
  public Constructor getConstructor(Class<?>... parameters);

  /**
   * Get the underlaying {@link Field}
   * 
   * @return the field
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
  public Field getField();

  /**
   * Is this field a {@link Set}?
   * 
   * @return true, if field is an instance of {@link Set}
   * @deprecated should no longer be used, isCollection() instead
   */
  @Deprecated
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
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
  public ITypeHandler getSubTypeHandler();

  /**
   * If the java field is a list / array / map the the sub type is returned
   * 
   * @return the subtype
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
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
   * @deprecated removed because of usage of jackson
   */
  @Deprecated
  public List<IProperty> getTypeParameters();

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

  /**
   * Get the {@link IColumnInfo} which is connected to the current field
   * 
   * @return
   */
  public IColumnInfo getColumnInfo();

  /**
   * Returns true, if the current field is a field annotated with {@link Id}
   * 
   * @return
   */
  public boolean isIdField();

  /**
   * If an {@link IEncoder} was defined by the annotation {@link Encoder}, it is returned here
   * 
   * @return valid instance of {@link IEncoder} or null, if not defined
   */
  IEncoder getEncoder();

  /**
   * Get the {@link Type} of the field. If this field uses generics to define its type. This is included as well.
   * 
   * @return the generic {@link Type} of the field
   */
  public Type getGenericType();

  /**
   * returns true if this property shall be ignored
   * 
   * @return
   */
  boolean isIgnore();

  /**
   * Checks wether the current definition is a character based property
   * 
   * @return
   */
  boolean isCharacterColumn();

  /**
   * checks wether the current definition is a numeric based property
   * 
   * @return
   */
  boolean isNumericColumn();

}
