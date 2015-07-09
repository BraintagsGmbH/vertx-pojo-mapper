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

package de.braintags.io.vertx.pojomapper.dataaccess.write.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class WriteResult implements IWriteResult {

  private IStoreObject<?> sto;
  private String id;

  public WriteResult(IStoreObject<?> sto, String id) {
    this.sto = sto;
    this.id = id;
  }

  @Override
  public IStoreObject<?> getStoreObject() {
    return sto;
  }

  @Override
  public Object getId() {
    return id;
  }

}
