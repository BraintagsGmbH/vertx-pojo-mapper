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
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;

/**
 * Translates the logic definitions into a propriate expression for the current datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IQueryLogicTranslator {

  /**
   * Translate the {@link QueryLogic} into the String expression fitting for the given datastore
   * 
   * @param logic
   *          the logic
   * @return the suitable expression
   */
  String translate(QueryLogic logic);

  /**
   * Returns true, if the given logig is meant to open parenthesis
   * 
   * @param logic
   *          the logic to be examined
   * @return true, if parenthesis should be opened
   */
  boolean opensParenthesis(QueryLogic logic);
}
