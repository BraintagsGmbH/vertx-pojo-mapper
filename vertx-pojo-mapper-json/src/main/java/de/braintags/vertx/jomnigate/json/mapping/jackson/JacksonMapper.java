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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractMapper;
import de.braintags.vertx.jomnigate.mapping.impl.MappedIdFieldImpl;

/**
 * An implementation of {@link IMapper} which uses jackson
 * 
 * @author Michael Remme
 * 
 */
public class JacksonMapper<T> extends AbstractMapper<T> {
  private BeanDescription beanDescription;
  private String keyGeneratorReference;
  private Class<?> creatorClass;

  public JacksonMapper(Class<T> mapperClass, JacksonMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    creatorClass = getEntity().polyClass() == Object.class ? getMapperClass() : getEntity().polyClass();
    this.keyGeneratorReference = creatorClass.getSimpleName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractMapper#validate()
   */
  @Override
  protected void validate() {
    super.validate();
    JsonTypeInfo ti = (JsonTypeInfo) getAnnotation(JsonTypeInfo.class);
    boolean polySet = getEntity().polyClass() != Object.class;
    if (ti != null && !polySet) {
      throw new MappingException(
          "If you are setting JsonTypeInfo, you must define Entity.polyClass as well in mapper : "
              + getMapperClass().getName());
    }
    if (polySet && ti == null) {
      throw new MappingException(
          "If you are setting Entity.polyClass, you must define JsonTypeInfo as well in mapper : "
              + getMapperClass().getName());
    }

  }

  @Override
  public final Annotation getAnnotation(Class<? extends Annotation> annotationClass) {
    Annotation ann = super.getAnnotation(annotationClass);
    if (ann == null) {
      ann = beanDescription.getClassAnnotations().get(annotationClass);
    }
    return ann;
  }

  /**
   * Get the class which is used, when an entity shall be generated from a json source by jackson. The class will be the
   * defined mapper class or the class defined by {@link Entity#polyClass()} for polymorphic entities
   * 
   * @return
   */
  public Class<?> getCreatorClass() {
    return creatorClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractMapper#computePersistentFields()
   */
  @Override
  protected void computePersistentFields() {
    ObjectMapper mapper = ((JsonDatastore) getMapperFactory().getDataStore()).getJacksonMapper();
    JavaType type = mapper.constructType(getMapperClass());
    this.beanDescription = mapper.getSerializationConfig().introspect(type);
    List<BeanPropertyDefinition> propertyList = beanDescription.findProperties();
    propertyList.forEach(def -> addMappedField(def.getFullName().getSimpleName(), new JacksonProperty(this, def)));
  }

  /**
   * Adds a mapped field into the list of properties
   * 
   * @param name
   * @param mf
   */
  protected void addMappedField(String name, IProperty mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (getIdField() != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      setIdField(createIdProperty(mf));
    }
    if (!mf.isIgnore()) {
      this.getMappedProperties().put(name, mf);
    }
  }

  protected IMappedIdField createIdProperty(IProperty property) {
    return new MappedIdFieldImpl(property);
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
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#getObjectFactory()
   */
  @Override
  public IObjectFactory getObjectFactory() {
    throw new UnsupportedOperationException("There is no need to call this method for this implementation");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapper#handleReferencedRecursive()
   */
  @Override
  public boolean handleReferencedRecursive() {
    throw new UnsupportedOperationException("There is no need to call this method for this implementation");
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
