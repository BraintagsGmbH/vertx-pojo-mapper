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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

  private List<ISearchCondition> conditions = new ArrayList<>();

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
  public List<ISearchCondition> getConditions() {
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
    result.append(StringUtils.join(getConditions(), " && "));
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
}
