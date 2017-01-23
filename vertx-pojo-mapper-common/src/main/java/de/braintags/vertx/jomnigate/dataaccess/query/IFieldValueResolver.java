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
 * Resolves any variable inside an {@link IVariableFieldCondition} with an actual object. The value can come from
 * anywhere, routing context, request parameter, field content of another POJO...
 * 
 * For example, the variable name might be "request.page". If the implementation of the resolver recognizes this
 * variable, it might look for a request parameter named "page" and return the value of the parameter.
 * The actual resolution is dependent on the implementation.
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
