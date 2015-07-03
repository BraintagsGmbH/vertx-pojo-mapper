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

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.dataaccess.IWrite;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * 
 * 
 * @author Michael Remme
 * @param <T>
 * 
 */

public class MongoWrite<T> extends AbstractMongoAccessObject<T> implements IWrite<T> {

  /**
   * 
   */
  public MongoWrite(final Class<T> mapperClass, MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.IWrite#save(java.lang.Object, io.vertx.core.Handler)
   */
  @Override
  public void save(T mapper, Handler<AsyncResult<T>> resultHandler) {
    throw new UnsupportedOperationException();
  }

}
