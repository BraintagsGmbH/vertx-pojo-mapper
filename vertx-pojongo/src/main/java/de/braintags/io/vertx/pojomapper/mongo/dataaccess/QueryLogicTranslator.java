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
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryLogicTranslator;

/**
 * Translates the logic defintions into a propriate expression
 * 
 * @author Michael Remme
 * 
 */
public class QueryLogicTranslator implements IQueryLogicTranslator {

  /**
   * Translate the {@link QueryOperator} into the String expression fitting for Mongo
   * 
   * @param logic
   *          the logic operator
   * @return the suitable expression
   */
  @Override
  public String translate(QueryLogic logic) {
    switch (logic) {
    case AND:
    case AND_OPEN:
      return "$and";
    case OR:
    case OR_OPEN:
      return "$or";

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
  @Override
  public boolean opensParenthesis(QueryLogic logic) {
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
