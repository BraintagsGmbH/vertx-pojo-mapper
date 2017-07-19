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

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.codegen.annotations.Nullable;

/**
 * An abstract implementation of {@link ISearchConditionContainer}
 *
 * @author sschmitt
 *
 */
public abstract class AbstractSearchConditionContainer implements ISearchConditionContainer {

  private final ConditionList conditions = new ConditionList();

  /**
   * Initializes the container with zero or more sub conditions
   *
   * @param conditions
   */
  public AbstractSearchConditionContainer(@Nullable ISearchCondition... conditions) {
    if (conditions != null)
      this.conditions.addAll(Arrays.asList(conditions));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer#getConditions()
   */
  @Override
  @JsonValue
  public ConditionList getConditions() {
    return conditions;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(getQueryLogic().name()).append(" [");
    result.append(StringUtils.join(getConditions(), " " + getQueryLogic() + " "));
    result.append("]");
    return result.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition#validate(de.braintags.vertx.jomnigate.mapping.
   * IMapper)
   */
  @Override
  public <T> void validate(IMapper<T> mapper) {
    getConditions().forEach(c -> c.validate(mapper));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
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
    AbstractSearchConditionContainer other = (AbstractSearchConditionContainer) obj;
    if (conditions == null) {
      if (other.conditions != null)
        return false;
    } else if (!conditions.equals(other.conditions))
      return false;
    return true;
  }

  /**
   * Wrapper around the search condition list to prevent type erasure when serializing with jackson
   */
  public static class ConditionList extends ArrayList<ISearchCondition> {
    private static final long serialVersionUID = 1L;
  }
}
