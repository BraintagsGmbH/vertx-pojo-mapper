/*
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
package de.braintags.vertx.jomnigate.dataaccess.query;

import de.braintags.vertx.jomnigate.dataaccess.query.exception.VariableSyntaxException;

/**
 * Resolves any variable inside a {@link IFieldCondition} with an actual object. The value can come from anywhere,
 * routing context, request parameter, field content of another POJO...
 *
 * @author sschmitt
 *
 */
@FunctionalInterface
public interface IFieldValueResolver {

  /**
   * Replace the field value variable with an actual object
   *
   * @param variableName
   *          the variable of the field condition, stripped of its identifying start- and end-tag
   * @return the actual object that should replace the variable
   */
  Object resolve(String variableName) throws VariableSyntaxException;

}
