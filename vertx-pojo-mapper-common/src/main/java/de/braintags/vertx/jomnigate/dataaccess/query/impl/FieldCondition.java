/*-
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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.json.Json;

/**
 * Simple implementation of {@link IFieldCondition}<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 *
 * @author sschmitt
 */

public class FieldCondition implements IFieldCondition {

  private IIndexedField field;
  private QueryOperator operator;
  private JsonNode value;

  private Map<Class<? extends IQueryExpression>, Object> cacheMap = new HashMap<>(1);

  /**
   * Creates a complete field condition
   *
   * @param field
   *          the field of this condition
   * @param logic
   *          the compare logic of this condition
   * @param value
   *          the value of this condition, can be null
   */
  @JsonCreator
  public FieldCondition(IIndexedField field, QueryOperator logic, @Nullable Object value) {
    this.field = field;
    this.operator = logic;
    this.value = transformObject(value);
  }

  public static JsonNode transformObject(@Nullable Object object) {
    return Json.mapper.convertValue(object, JsonNode.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition#setIntermediateResult(java.lang.Class,
   * java.lang.Object)
   */
  @Override
  @JsonIgnore
  public void setIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass, Object result) {
    cacheMap.put(queryExpressionClass, result);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition#getIntermediateResult(java.lang.Class)
   */
  @Override
  @JsonIgnore
  public Object getIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass) {
    return cacheMap.get(queryExpressionClass);
  }

  /**
   * @return the POJO field of this condition
   */
  @Override
  public IIndexedField getField() {
    return field;
  }

  /**
   * @return the compare logic of this condition
   */
  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  /**
   * @return the value of this condition, can be null
   */
  @Override
  public JsonNode getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return field + " " + operator + " " + String.valueOf(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition#validate(de.braintags.vertx.jomnigate.mapping.
   * IMapper)
   */
  @Override
  public <T> void validate(IMapper<T> mapper) {
    String fieldName = field.getFieldName();
    int dot = fieldName.indexOf('.');
    if (dot > 0) { // for now we are checking the base field only
      fieldName = fieldName.substring(0, dot);
    }
    IProperty p = mapper.getField(fieldName);
    if (p == null) {
      throw new NoSuchFieldException(mapper, fieldName);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((operator == null) ? 0 : operator.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FieldCondition other = (FieldCondition) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (operator != other.operator)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }
}
