/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Carrier for a result handler
 * 
 * @author Michael Remme
 * @param <E>
 *          the underlaying class, which shall be delivered to the Handler as {@link AsyncResult}
 * 
 */

public class ResultObject<E> extends ErrorObject<E> {
  private boolean resultDefined = false;
  private E result;

  /**
   * Constructor with a {@link Handler}, which will be informed about the result, when the
   * method {@link #setResult(Object)} is called
   * 
   * @param handler
   *          the handler to be informed
   */
  public ResultObject(Handler<AsyncResult<E>> handler) {
    super(handler);
  }

  /**
   * @return the result
   */
  public final E getResult() {
    return result;
  }

  /**
   * @param result
   *          the result to set
   */
  public final void setResult(E result) {
    this.result = result;
    this.resultDefined = true;
    handleResult();
  }

  /**
   * @return the resultDefined
   */
  public final boolean isResultDefined() {
    return resultDefined;
  }

  /**
   * If an error occured or a result exists, the handler will be called with a succeedded or error {@link Future}
   * 
   * @param handler
   */
  private boolean handleResult() {
    if (super.handleError()) {
      return true;
    } else if (isResultDefined()) {
      getHandler().handle(Future.succeededFuture(result));
      return true;
    }
    return false;
  }

}
