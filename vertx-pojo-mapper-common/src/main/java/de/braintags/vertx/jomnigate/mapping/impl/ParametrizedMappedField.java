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
package de.braintags.vertx.jomnigate.mapping.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ParametrizedMappedField describes a field with type paramters
 * 
 * @author Michael Remme
 * 
 */
public class ParametrizedMappedField extends MappedField {
  private MappedField parent;
  private ParameterizedType pType;

  /**
   * 
   * @param type
   *          the {@link ParameterizedType} to be used here
   * @param parent
   *          the main definition of the field
   */
  public ParametrizedMappedField(final ParameterizedType type, final MappedField parent) {
    super(type, (Mapper) parent.getMapper());
    this.parent = parent;
    pType = type;
    final Class<?> rawClass = (Class<?>) type.getRawType();
    setIsSet(Set.class.isAssignableFrom(rawClass));
    setIsMap(Map.class.isAssignableFrom(rawClass));
    setMapKeyType(getMapKeyClass());
    setSubType(getSubType());
  }

  /**
   * @param type
   *          the {@link Type} to be used
   * @param parent
   *          the main definition of the field
   */
  public ParametrizedMappedField(final Type type, final MappedField parent) {
    super(type, (Mapper) parent.getMapper());
    this.parent = parent;
  }

  @Override
  public Class<?> getType() {
    if (pType == null) {
      return super.getType();
    } else if (isMap()) {
      return Map.class;
    } else {
      return List.class;
    }
  }

  @Override
  public Class<?> getMapKeyClass() {
    return (Class<?>) (isMap() ? pType.getActualTypeArguments()[0] : null);
  }

  @Override
  public Type getSubType() {
    return pType != null ? pType.getActualTypeArguments()[isMap() ? 1 : 0] : null;
  }

  @Override
  public boolean isSingleValue() {
    return false;
  }

  /**
   * Get the parent
   * 
   * @return the parent instance of {@link MappedField}
   */
  public MappedField getParent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.MappedField#getFullName()
   */
  @Override
  public String getFullName() {
    return getParent().getFullName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.MappedField#getName()
   */
  @Override
  public String getName() {
    return getParent().getName();
  }
}
