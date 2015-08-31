/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.query;

/**
 * Defines the operators for a query
 * 
 * @author Michael Remme
 * 
 */

public enum QueryOperator {
  EQUALS,
  NOT_EQUALS,
  LARGER,
  SMALLER,
  LARGER_EQUAL,
  SMALLER_EQUAL,
  IN,
  NOT_IN;

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