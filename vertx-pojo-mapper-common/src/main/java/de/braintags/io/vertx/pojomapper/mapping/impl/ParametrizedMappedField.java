/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class ParametrizedMappedField extends MappedField {
  private MappedField parent;
  private ParameterizedType pType;

  /**
   * @param field
   * @param accessor
   * @param mapper
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

}
