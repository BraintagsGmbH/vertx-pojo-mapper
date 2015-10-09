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

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;

/**
 * Translates the logic defintions into a propriate expression
 * 
 * @author Michael Remme
 * 
 */

public class QueryLogicTranslator {

  /**
   * 
   */
  private QueryLogicTranslator() {
  }

  /**
   * Translate the {@link QueryLogic} into the String expression fitting for sql
   * 
   * @param logic
   *          the logic
   * @return the suitable expression
   */
  public static String translate(QueryLogic logic) {
    switch (logic) {
    case AND:
      return "AND";
    case OR:
      return "OR";

    default:
      throw new UnsupportedOperationException("No translator for " + logic);
    }
  }
}
