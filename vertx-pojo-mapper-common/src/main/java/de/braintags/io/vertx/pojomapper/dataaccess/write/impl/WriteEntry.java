/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.write.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * Default implementation of {@link IWriteEntry}
 * 
 * @author Michael Remme
 * 
 */

public class WriteEntry implements IWriteEntry {
  private final IStoreObject<?> sto;
  private final String id;
  private final WriteAction action;

  WriteEntry(IStoreObject<?> sto, String id, WriteAction action) {
    this.sto = sto;
    this.id = id;
    this.action = action;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry#getStoreObject()
   */
  @Override
  public IStoreObject<?> getStoreObject() {
    return sto;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry#getId()
   */
  @Override
  public Object getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry#getAction()
   */
  @Override
  public WriteAction getAction() {
    return action;
  }

}
