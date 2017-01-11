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
package de.braintags.io.vertx.pojomapper.dataaccess.query;

/**
 * Defines the operators for a query
 * 
 * @author Michael Remme
 * 
 */

public enum QueryOperator {
  EQUALS(false),
  NOT_EQUALS(false),

  LARGER(false),
  SMALLER(false),
  LARGER_EQUAL(false),
  SMALLER_EQUAL(false),

  STARTS(false),
  ENDS(false),
  CONTAINS(false),

  NEAR(false),

  IN(true),
  NOT_IN(true);

  private final boolean multiValueOperator;

  private QueryOperator(boolean multiValueOperator) {
    this.multiValueOperator = multiValueOperator;
  }

  /**
   * @return if the value of conditions with this operator must consist of multiple values
   */
  public boolean isMultiValueOperator() {
    return multiValueOperator;
  }

}

// NEAR("$near"),
// NEAR_SPHERE("$nearSphere"),
// WITHIN("$within"),
// WITHIN_CIRCLE("$center"),
// WITHIN_CIRCLE_SPHERE("$centerSphere"),
// WITHIN_BOX("$box"),
// GEO_WITHIN("$geoWithin"),
// EXISTS("$exists"),
// TYPE("$type"),
// NOT("$not"),
// MOD("$mod"),
// SIZE("$size"),
// ALL("$all"),
// ELEMENT_MATCH("$elemMatch"),
// WHERE("$where")