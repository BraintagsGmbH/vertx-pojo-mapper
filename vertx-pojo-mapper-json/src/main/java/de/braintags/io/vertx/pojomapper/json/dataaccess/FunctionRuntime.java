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
package de.braintags.io.vertx.pojomapper.json.dataaccess;

import de.braintags.io.vertx.pojomapper.annotation.field.Function;

/**
 * FunctionRuntime is used as transporter object, when a field is marked with the annotation {@link Function}
 * 
 * @author Michael Remme
 * 
 */

public class FunctionRuntime {
  private Function function;
  private boolean execute;

  public FunctionRuntime(Function function, Object fieldValue) {
    this.function = function;
    switch (function.fieldState()) {
    case ALL:
      execute = true;
      break;

    case EMPTY:
      execute = fieldValue == null || fieldValue.hashCode() == 0;
      break;

    case FILLED:
      execute = fieldValue != null && fieldValue.hashCode() != 0;
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  /**
   * The function definition taken from the field, which was annotated
   * 
   * @return the function
   */
  public final Function getFunction() {
    return function;
  }

  /**
   * The function definition taken from the field, which was annotated
   * 
   * @param function
   *          the function to set
   */
  public final void setFunction(Function function) {
    this.function = function;
  }

  /**
   * defines, wether the function shall be executed
   * 
   * @return the execute
   */
  public final boolean isExecute() {
    return execute;
  }

  /**
   * defines, wether the function shall be executed
   * 
   * @param execute
   *          the execute to set
   */
  public final void setExecute(boolean execute) {
    this.execute = execute;
  }

}
