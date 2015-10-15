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
    case AND_OPEN:
      return "AND";
    case OR: // both return the same, parenthesis are set in Rambler
    case OR_OPEN:
      return "OR";

    default:
      throw new UnsupportedOperationException("No translator for " + logic);
    }
  }

  /**
   * Returns true, if the given logis is meant to open parenthesis
   * 
   * @param logic
   *          the logic to be examined
   * @return true, if parenthesis should be opened
   */
  public static boolean opensParenthesis(QueryLogic logic) {
    switch (logic) {
    case AND:
    case OR:
      return false;

    case AND_OPEN:
    case OR_OPEN:
      return true;

    default:
      throw new UnsupportedOperationException("No translator for " + logic);
    }
  }

}
