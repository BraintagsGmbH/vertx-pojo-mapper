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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * Default implementation of {@link IWriteResult}
 * 
 * @author Michael Remme
 * 
 */

public class WriteResult implements IWriteResult {
  private List<IWriteEntry> resultList = new ArrayList<IWriteEntry>();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult#getResult()
   */
  @Override
  public Iterator<IWriteEntry> iterator() {
    return resultList.iterator();
  }

  /**
   * Add a new entry
   * 
   * @param entry
   *          the entry to be added
   */
  private void addEntry(IWriteEntry entry) {
    resultList.add(entry);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult#addEntry(de.braintags.io.vertx.pojomapper.mapping
   * .IStoreObject, java.lang.String, de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction)
   */
  @Override
  public void addEntry(IStoreObject<?> sto, String id, WriteAction action) {
    addEntry(new WriteEntry(sto, id, action));
  }

  @Override
  public int size() {
    return resultList.size();
  }

}
