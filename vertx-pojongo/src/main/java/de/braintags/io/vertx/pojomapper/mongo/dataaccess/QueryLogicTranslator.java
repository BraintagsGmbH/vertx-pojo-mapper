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
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class QueryLogicTranslator {

  /**
   * 
   */
  public QueryLogicTranslator() {
  }

  /**
   * Translate the {@link QueryOperator} into the String expression fitting for Mongo
   * 
   * @param op
   *          the operator
   * @return the suitable expression
   */
  public static String translate(QueryLogic logic) {
    switch (logic) {
    case AND:
      return "$and";
    case OR:
      return "$or";

    default:
      throw new UnsupportedOperationException("No translator for " + logic);
    }
  }
}
