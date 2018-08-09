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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryLogic;

/**
 * Represents a container that joins search conditions with an {@link QueryLogic#NOR}<br>
 * <br>
 * Copyright: Copyright (c) 19.07.2017 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author mpluecker
 */
public class QueryNor extends AbstractSearchConditionContainer {

  /**
   * Initializes the container with zero or more sub conditions that will be connected with {@link QueryLogic#NOR}
   * 
   * @param conditions
   */
  @JsonCreator
  public QueryNor(final ISearchCondition... searchConditions) {
    super(searchConditions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer#getQueryLogic()
   */
  @Override
  @JsonIgnore
  public QueryLogic getQueryLogic() {
    return QueryLogic.NOR;
  }

}
