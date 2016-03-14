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
package de.braintags.io.vertx.pojomapper.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.exception.InsertException;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Default implementation of {@link IWriteResult}
 * 
 * @author Michael Remme
 * 
 */

public class WriteResult implements IWriteResult {
  private static final Logger logger = LoggerFactory.getLogger(WriteResult.class);
  private List<IWriteEntry> resultList = new ArrayList<IWriteEntry>();
  @SuppressWarnings("rawtypes")
  private List insertedIds = new ArrayList();

  public WriteResult() {
  }

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
  @SuppressWarnings("unchecked")
  protected void addEntry(IWriteEntry entry) {
    resultList.add(entry);
    if (entry.getAction().equals(WriteAction.INSERT)) {
      if (insertedIds.contains(entry.getId())) {
        throw new InsertException(String.format("Trial to insert duplicate ID. Existing IDs: %s | new Id: %s ",
            String.valueOf(insertedIds), String.valueOf(entry.getId())));
      } else {
        insertedIds.add(entry.getId());
      }
    }
  }

  @Override
  public void addEntry(IStoreObject<?> sto, Object id, WriteAction action) {
    addEntry(new WriteEntry(sto, id, action));
  }

  @Override
  public int size() {
    return resultList.size();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (IWriteEntry entry : resultList) {
      builder.append(entry.toString()).append("\n");
    }
    return builder.toString();
  }

}
