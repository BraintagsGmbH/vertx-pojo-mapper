/*
 * Copyright 2014 Red Hat, Inc.
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

package de.braintags.io.vertx.pojomapper.typehandler;

/**
 * Used to store the result of the work of an {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */
public interface ITypeHandlerResult {

  /**
   * Set the result of the work of an {@link ITypeHandler}
   * 
   * @param result
   *          the result to be set
   */
  void setResult(Object result);

  /**
   * Get the result of the work of an {@link ITypeHandler}
   * 
   * @return the result
   */
  Object getResult();

  /**
   * If an error occured, it's stored here
   * 
   * @param e
   *          the error
   */
  void setException(Throwable e);

  /**
   * If an error occured, it's stored here
   * 
   * @return the error
   */
  Throwable getException();

  /**
   * If the instance contains an Exception, it is thrown as RuntimeException
   */
  void validate();

}
