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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.field.ConcreteClass;
import de.braintags.io.vertx.pojomapper.annotation.field.ConstructorArguments;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Function;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.util.ClassUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Represents a field, which shall be mapped into an {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

public class MappedField implements IField {
  private static final Logger LOGGER = LoggerFactory.getLogger(MappedField.class);

  private IPropertyAccessor accessor;
  private Field field;
  private Mapper mapper;
  private ITypeHandler typeHandler;
  private ITypeHandler subTypeHandler;
  private boolean subTypeHandlerComputed = false;
  private IPropertyMapper propertyMapper;
  private final List<IField> typeParameters = new ArrayList<IField>();

  /**
   * Annotations which shall be checked for a field definition
   */
  private static final List<Class<? extends Annotation>> FIELD_ANNOTATIONS = Arrays.asList(Id.class, Property.class,
      Referenced.class, Embedded.class, ConcreteClass.class, Function.class);
  /**
   * If for the current field an Annotation {@link Embedded} or {@link Referenced} is defined, then it is stored in here
   */
  private Annotation embedRef;

  /**
   * Class annotations which were found inside the current definition
   */
  private final Map<Class<? extends Annotation>, Annotation> existingClassAnnotations = new HashMap<Class<? extends Annotation>, Annotation>();
  private Class<?> realType;
  private Type genericType;
  private boolean isSet;
  private boolean isMap; // indicated if it implements Map interface
  private boolean isArray; // indicated if it is an Array
  private boolean isCollection; // indicated if the collection is a list)
  private boolean isSingleValue = true; // indicates the field is a single value

  private Type mapKeyType;
  private Type subType;
  private Map<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();

  /**
   * Constructor which creates a new instance by reading informations from the given {@link Field}
   * 
   * @param field
   *          the field to be used
   * @param accessor
   *          the {@link IPropertyAccessor} which shall be used by the current instance
   * @param mapper
   *          the parent {@link IMapper}
   */
  public MappedField(Field field, IPropertyAccessor accessor, Mapper mapper) {
    this.accessor = accessor;
    this.field = field;
    field.setAccessible(true);
    realType = field.getType();
    this.mapper = mapper;
    genericType = field.getGenericType();
    init();
  }

  /**
   * Constructor which build a new instance from the given class
   * 
   * @param type
   *          the underlaying class for the new instance
   * @param mapper
   *          the parent {@link IMapper}
   */
  public MappedField(Type type, Mapper mapper) {
    this.mapper = mapper;
    genericType = type;
    computeType();
    // computeSubTypeHandler();
  }

  protected void init() {
    computeAnnotations();
    propertyMapper = computePropertyMapper();
    computeType();
    computeMultivalued();
    // computeSubTypeHandler();
  }

  private void computeSubTypeHandler() {
    if (!subTypeHandlerComputed) {
      if (getSubClass() != null && getSubClass() != Object.class) {
        ITypeHandler th = getTypeHandler();
        if (th != null)
          subTypeHandler = th.getSubTypeHandler(getSubClass(), getEmbedRef());
      }
      subTypeHandlerComputed = true;
    }
  }

  protected final IPropertyMapper computePropertyMapper() {
    return mapper.getMapperFactory().getPropertyMapperFactory().getPropertyMapper(this);
  }

  // TODO Yet needed?
  @SuppressWarnings("rawtypes")
  private Constructor computeConstructor() {
    Constructor<?> constructor = null;
    Class<?> type = null;
    // get the first annotation with a concreteClass that isn't Object.class
    Annotation cA = getAnnotation(ConcreteClass.class);
    if (cA != null) {
      Class<?> conClass = ((ConcreteClass) cA).value();
      if (conClass != null && !(conClass.equals(Object.class))) {
        type = conClass;
      }
    }

    if (type != null) {
      // TODO change this. First check for the existence of ConstructorArguments and build a Constructor from that. Then
      // check for default constructor
      try {
        constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
      } catch (NoSuchMethodException e) {
        if (!hasAnnotation(ConstructorArguments.class)) {
          throw new MappingException(String.format(
              "Field %s has no default constructor and no arguments defined. Use annotation ConstructorArguments!",
              getName()), e);
        }
      }
    } else {
      // see if we can create instances of the type used for declaration
      type = getType();

      // short circuit to avoid wasting time throwing an exception trying to get a constructor we know doesnt exist
      if (type == List.class || type == Map.class) {
        return null;
      }

      if (type != null) {
        try {
          constructor = type.getDeclaredConstructor();
          constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
          LOGGER.warn("unaccessible constructor", e);
        } catch (SecurityException e) {
          LOGGER.warn("unaccessible constructor", e);
        }
      }
    }
    return constructor;
  }

  protected void computeMultivalued() {
    if (realType.isArray() || Collection.class.isAssignableFrom(realType) || Map.class.isAssignableFrom(realType)
        || GenericArrayType.class.isAssignableFrom(genericType.getClass())) {

      isSingleValue = false;

      isMap = Map.class.isAssignableFrom(realType);
      isSet = Set.class.isAssignableFrom(realType);
      // for debugging
      isCollection = Collection.class.isAssignableFrom(realType);
      isArray = realType.isArray();

      // for debugging with issue
      if (!isMap && !isSet && !isCollection && !isArray) {
        throw new MappingException("type is not a map/set/collection/array : " + realType);
      }

      // get the subtype T, T[]/List<T>/Map<?,T>; subtype of Long[], List<Long> is Long
      subType = (realType.isArray()) ? realType.getComponentType()
          : ClassUtil.getParameterizedType(field, isMap ? 1 : 0);

      if (isMap) {
        mapKeyType = ClassUtil.getParameterizedType(field, 0);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void computeType() {
    ParameterizedType pt = null;
    TypeVariable<GenericDeclaration> tv = null;
    if (genericType instanceof TypeVariable) {
      tv = (TypeVariable<GenericDeclaration>) genericType;
      final Class<?> typeArgument = ClassUtil.getTypeArgument(mapper.getMapperClass(), tv);
      if (typeArgument != null) {
        realType = typeArgument;
      }
    } else if (genericType instanceof ParameterizedType) {
      pt = (ParameterizedType) genericType;
      final Type[] types = pt.getActualTypeArguments();
      realType = ClassUtil.toClass(pt);

      for (Type type : types) {
        if (type instanceof ParameterizedType) {
          typeParameters.add(new ParametrizedMappedField((ParameterizedType) type, this));
        } else {
          if (type instanceof WildcardType) {
            type = ((WildcardType) type).getUpperBounds()[0];
          }
          typeParameters.add(new ParametrizedMappedField(type, this));
        }
      }
    } else if (genericType instanceof WildcardType) {
      final WildcardType wildcardType = (WildcardType) genericType;
      final Type[] types = wildcardType.getUpperBounds();
      realType = ClassUtil.toClass(types[0]);
    } else if (genericType instanceof Class) {
      realType = (Class<?>) genericType;
    } else if (genericType instanceof GenericArrayType) {
      realType = (Class<?>) ((GenericArrayType) genericType).getGenericComponentType();
    }

    if (Object.class.equals(realType) && (tv != null || pt != null)) {
      LOGGER.warn("Parameterized types are treated as untyped Objects. See field '" + field.getName() + "' on "
          + field.getDeclaringClass());
    }

    if (realType == null) {
      throw new MappingException(format("A type could not be found for the field %s.%s", getType(), getField()));
    }
  }

  protected void computeAnnotations() {
    for (Class<? extends Annotation> annClass : FIELD_ANNOTATIONS) {
      Annotation ann = field.getAnnotation(annClass);
      if (ann != null)
        existingClassAnnotations.put(annClass, ann);
    }
    if (hasAnnotation(Referenced.class))
      embedRef = getAnnotation(Referenced.class);
    else if (hasAnnotation(Embedded.class))
      embedRef = getAnnotation(Embedded.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getPropertyDescriptor()
   */
  @Override
  public IPropertyAccessor getPropertyAccessor() {
    return accessor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getTypeHandler()
   */
  @Override
  public ITypeHandler getTypeHandler() {
    if (typeHandler == null)
      typeHandler = mapper.getMapperFactory().getTypeHandlerFactory().getTypeHandler(this);
    return typeHandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getAnnotation(java.lang.Class)
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
    FIELD_ANNOTATIONS.add(annotation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#isSet()
   */
  @Override
  public boolean isSet() {
    return isSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#setIsSet(boolean)
   */
  public void setIsSet(final boolean isSet) {
    this.isSet = isSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getMapKeyClass()
   */
  @Override
  public Class<?> getMapKeyClass() {
    return ClassUtil.toClass(mapKeyType);
  }

  void setMapKeyType(final Class<?> mapKeyType) {
    this.mapKeyType = mapKeyType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#isMap()
   */
  @Override
  public boolean isMap() {
    return isMap;
  }

  /**
   * Defines wether the field is an instance of {@link Map}
   * 
   * @param isMap
   *          the isMap to set
   */
  void setIsMap(boolean isMap) {
    this.isMap = isMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getType()
   */
  @Override
  public Class<?> getType() {
    return realType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getSubClass()
   */
  @Override
  public Class<?> getSubClass() {
    return ClassUtil.toClass(getSubType());
  }

  /**
   * Define the syb type of the instance
   * 
   * @param subType
   *          the sub type
   */
  void setSubType(final Type subType) {
    this.subType = subType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getSubType()
   */
  @Override
  public Type getSubType() {
    return subType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#isSingleValue()
   */
  @Override
  public boolean isSingleValue() {
    if (!isSingleValue && !isMap && !isSet && !isArray && !isCollection) {
      throw new IllegalArgumentException("Not single, but none of the types that are not-single.");
    }
    return isSingleValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getField()
   */
  @Override
  public Field getField() {
    return field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getTypeParameters()
   */
  @Override
  public List<IField> getTypeParameters() {
    return typeParameters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#isArray()
   */
  @Override
  public boolean isArray() {
    return isArray;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#isCollection()
   */
  @Override
  public boolean isCollection() {
    return isCollection;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#hasAnnotation(java.lang.Class)
   */
  @Override
  public boolean hasAnnotation(final Class<? extends Annotation> ann) {
    return existingClassAnnotations.containsKey(ann);
  }

  /**
   * returns the full name of the class plus java field name
   * 
   * @return the name
   */
  @Override
  public String getFullName() {
    return field.getDeclaringClass().getName() + "." + field.getName();
  }

  @Override
  public String toString() {
    return getFullName();
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public IPropertyMapper getPropertyMapper() {
    return propertyMapper;
  }

  @Override
  public Annotation getEmbedRef() {
    return embedRef;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getConstructor(java.lang.Class[])
   */
  @Override
  public Constructor<?> getConstructor(Class<?>... parameters) {
    String code = generateKey(parameters);
    if (constructors.containsKey(code))
      return constructors.get(code);
    Class<?> clz = getType();
    Constructor<?> constructor = null;
    try {
      constructor = clz.getDeclaredConstructor(parameters);
      constructors.put(code, constructor);
    } catch (NoSuchMethodException | SecurityException e) {
      LOGGER.debug("unaccessible constructor because of " + e);
      constructors.put(code, constructor);
    }
    return constructor;
  }

  private String generateKey(Class<?>... parameters) {
    if (parameters.length == 0)
      return "default";
    String key = "";
    for (Class<?> cls : parameters) {
      key += cls.getName();
    }
    return key;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getSubTypeHandler()
   */
  @Override
  public ITypeHandler getSubTypeHandler() {
    computeSubTypeHandler();
    return subTypeHandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IField#getColumnInfo()
   */
  @Override
  public IColumnInfo getColumnInfo() {
    return getMapper().getTableInfo().getColumnInfo(this);
  }
}
