/*-
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

/**
 * A single condition statement of a query, to filter the result based on the value of a field<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public interface IFieldCondition extends ISearchCondition {

  /**
   * @return the field of the condition
   */
  public String getField();

  /**
   * @return the operator of the condition
   */
  public QueryOperator getOperator();

  /**
   * @return the value of the condition
   */
  public Object getValue();

}
