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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultEmbeddedMapper extends AbstractSubobjectMapper implements IEmbeddedMapper {

  /**
   * 
   */
  public DefaultEmbeddedMapper() {
  }

  @Override
  public void writeSingleValue(Object referencedObject, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Object>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

  @Override
  public void readSingleValue(Object dbValue, IField field, Class<?> mapperClass, Handler<AsyncResult<Object>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

}
