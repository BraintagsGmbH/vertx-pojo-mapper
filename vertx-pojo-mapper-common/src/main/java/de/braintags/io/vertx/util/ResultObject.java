/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Carrier for a result handler
 * 
 * @author Michael Remme
 * 
 */

public class ResultObject<E> extends ErrorObject<E> {
  private boolean resultDefined = false;
  private E result;

  /**
   * 
   */
  public ResultObject() {
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
  public boolean handleResult(Handler<AsyncResult<E>> handler) {
    if (super.handleError(handler)) {
      return true;
    } else if (isResultDefined()) {
      handler.handle(Future.succeededFuture(result));
      return true;
    }
    return false;
  }

}
