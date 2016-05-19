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

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;

/**
 * Translates operator definitions into propriate expressions for the datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IQueryOperatorTranslator {

  /**
   * Translate the given {@link QueryOperator} into an expression fitting for sql
   * 
   * @param op
   *          the {@link QueryOperator} to be translated
   * @return a suitable String expression
   */
  String translate(QueryOperator op);

  /**
   * Translates the value in dependency to the operator
   * 
   * @param operator
   *          the opertator for decision
   * @param value
   *          the value
   * @return a value, which might be transformed depending on the given operator
   */
  Object translateValue(QueryOperator operator, Object value);

}
