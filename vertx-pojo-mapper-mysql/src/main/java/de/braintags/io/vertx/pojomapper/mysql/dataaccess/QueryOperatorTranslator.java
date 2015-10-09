/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;

/**
 * Translates operator definitions into propriate expressions for the datastore
 * 
 * @author Michael Remme
 * 
 */

public class QueryOperatorTranslator {

  /**
   * 
   */
  private QueryOperatorTranslator() {
  }

  /**
   * Translate the given {@link QueryOperator} into an expression fitting for sql
   * 
   * @param op
   * @return
   */
  public static String translate(QueryOperator op) {
    switch (op) {
    case EQUALS:
      return "=";
    case NOT_EQUALS:
      return "!=";
    case LARGER:
      return ">";
    case LARGER_EQUAL:
      return ">=";
    case SMALLER:
      return "<";
    case SMALLER_EQUAL:
      return "<=";
    case IN:
      return "IN";
    case NOT_IN:
      return "NOT IN";

    default:
      throw new UnsupportedOperationException("No translator for " + op);
    }
  }
}
