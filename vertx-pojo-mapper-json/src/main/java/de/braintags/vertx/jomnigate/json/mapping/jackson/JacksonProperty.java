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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractProperty;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;

/**
 * An implementation of IProperty which uses
 * 
 * @author Michael Remme
 * 
 */
public class JacksonProperty extends AbstractProperty {
  private BeanPropertyDefinition beanDefinition;
  private boolean isSingleValue;
  private IPropertyAccessor propertyAccessor;
  private IPropertyMapper propertyMapper;

  /**
   * 
   */
  public JacksonProperty(JacksonMapper<?> mapper, BeanPropertyDefinition definition) {
    super(mapper);
    this.beanDefinition = definition;
    init();
  }

  protected void init() {
    isSingleValue = !isMap() && !isCollection() && !isArray();
    propertyAccessor = new JacksonPropertyAccessor(beanDefinition);
    propertyMapper = new JacksonPropertyMapper((JsonDatastore) getMapper().getMapperFactory().getDataStore());
    computeEncoder();
  }

  /**
   * Get the underlaying instance of {@link BeanPropertyDefinition}
   * 
   * @return
   */
  public BeanPropertyDefinition getBeanPropertyDefinition() {
    return beanDefinition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getName()
   */
  @Override
  public String getName() {
    return beanDefinition.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getFullName()
   */
  @Override
  public String getFullName() {
    return beanDefinition.getField().getFullName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isMap()
   */
  @Override
  public boolean isMap() {
    return beanDefinition.getAccessor().getType().isMapLikeType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getMapKeyClass()
   */
  @Override
  public Class<?> getMapKeyClass() {
    return isMap() ? beanDefinition.getAccessor().getType().getKeyType().getRawClass() : null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getType()
   */
  @Override
  public Class<?> getType() {
    return beanDefinition.getAccessor().getType().getRawClass();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getSubClass()
   */
  @Override
  public Class<?> getSubClass() {
    if (beanDefinition.getAccessor().getType().hasGenericTypes()) {
      return beanDefinition.getAccessor().getType().getContentType().getRawClass();
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isSingleValue()
   */
  @Override
  public boolean isSingleValue() {
    return isSingleValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isArray()
   */
  @Override
  public boolean isArray() {
    return beanDefinition.getAccessor().getType().isArrayType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isCollection()
   */
  @Override
  public boolean isCollection() {
    return beanDefinition.getAccessor().getType().isCollectionLikeType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getGenericType()
   */
  @Override
  public Type getGenericType() {
    return beanDefinition.getAccessor().getType().getContentType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isIgnore()
   */
  @Override
  public boolean isIgnore() {
    return hasAnnotation(JsonIgnore.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getAnnotation(java.lang.Class)
   */
  @Override
  public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
    return beanDefinition.getAccessor().getAnnotation(annotationClass);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#hasAnnotation(java.lang.Class)
   */
  @Override
  public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
    return beanDefinition.getAccessor().getAnnotation(annotationClass) != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getPropertyAccessor()
   */
  @Override
  public IPropertyAccessor getPropertyAccessor() {
    return propertyAccessor;
  }

  /*
   * ##################################
   * UNSUPPORTED BY THIS IMPLEMENTATION
   * ##################################
   */

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getTypeHandler()
   */
  @Override
  public ITypeHandler getTypeHandler() {
    throw new UnsupportedOperationException("this implementation should not use ITypeHandler");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getPropertyMapper()
   */
  @Override
  public IPropertyMapper getPropertyMapper() {
    return propertyMapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getField()
   */
  @Override
  public Field getField() {
    throw new UnsupportedOperationException("this implementation should not use Field");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#isSet()
   */
  @Override
  public boolean isSet() {
    throw new UnsupportedOperationException("should not be used anymore: isSet");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getSubTypeHandler()
   */
  @Override
  public ITypeHandler getSubTypeHandler() {
    throw new UnsupportedOperationException("should not be used anymore: ITypeHandler");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getSubType()
   */
  @Override
  public Type getSubType() {
    throw new UnsupportedOperationException("should not be used anymore: getSubType");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getTypeParameters()
   */
  @Override
  public List<IProperty> getTypeParameters() {
    throw new UnsupportedOperationException("should not be used anymore: getTypeParameters");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IProperty#getConstructor(java.lang.Class[])
   */
  @Override
  public Constructor getConstructor(Class<?>... parameters) {
    throw new UnsupportedOperationException("should not be used anymore: getConstructor");
  }

}
