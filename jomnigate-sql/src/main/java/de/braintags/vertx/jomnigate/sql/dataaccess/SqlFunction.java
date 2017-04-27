/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.sql.dataaccess;

/**
 * An SqlFunction can be used as return type by TypeHandlers as value
 * 
 * @author Michael Remme
 * 
 */
public class SqlFunction {
  private String functionName;
  private String content;

  public SqlFunction(String name, String content) {
    this.functionName = name;
    this.content = content;
  }

  /**
   * The name of the function to be used
   * 
   * @return the functionName
   */
  public String getFunctionName() {
    return functionName;
  }

  /**
   * The content of the function to be used
   * 
   * @return the content
   */
  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return content;
  }
}
