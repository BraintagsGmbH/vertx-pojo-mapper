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
 * The ITypehandler is responsible to change original values into the required format of the used datastore
 * 
 * @author Michael Remme
 * 
 */

public interface ITypeHandler {

  /**
   * This method is called when a value is read from a field of a mapped object and must change the value into the
   * needed format and type
   * 
   * @param source
   * @return
   */
  Object fromStore(Object source);

  /**
   * This method is called when an object shall be persisted into the datastore and shall change the value into the
   * needed format of the datastore
   * 
   * @param source
   * @return
   */
  Object intoStore(Object source);
}
