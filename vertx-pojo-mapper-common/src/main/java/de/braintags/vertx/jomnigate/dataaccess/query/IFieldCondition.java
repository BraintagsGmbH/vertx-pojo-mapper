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

import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;

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
  public IIndexedField getField();

  /**
   * @return the operator of the condition
   */
  public QueryOperator getOperator();

  /**
   * @return the value of the condition
   */
  public Object getValue();

  /**
   * Cache the intermediate result of the conversion from this object to the native database object
   *
   * @param queryExpressionClass
   *          the class of the query expression that did this conversion. The class is used as cache key to enable
   *          storing different results for different datastores
   * @param result
   *          the intermediate result of the conversion of this single object
   */
  public void setIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass, Object result);

  /**
   * Get the cached result of the conversion, if it was converted before
   *
   * @param queryExpressionClass
   *          the class of the query expression that did this conversion. The class is used as cache key to enable
   *          storing different results for different datastores
   * @return the intermediate result, if it was generated and cached, null otherwise
   */
  public Object getIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass);

}
