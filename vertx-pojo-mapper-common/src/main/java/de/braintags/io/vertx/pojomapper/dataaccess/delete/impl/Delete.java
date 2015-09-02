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

package de.braintags.io.vertx.pojomapper.dataaccess.delete.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;

/**
 * Abstract implementation of {@link IDelete}
 * 
 * @author Michael Remme
 * 
 */

public abstract class Delete<T> extends AbstractDataAccessObject<T> implements IDelete<T> {
  private IQuery<T> query;
  private ArrayList<T> recordList = new ArrayList<T>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Delete(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#setQuery(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IQuery)
   */
  @Override
  public void setQuery(IQuery<T> query) {
    if (!recordList.isEmpty())
      throw new UnsupportedOperationException(
          "You can only use ONE source for deletion, or an IQuery or a list of instances");
    this.query = query;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#add(java.lang.Object)
   */
  @Override
  public void add(T record) {
    if (query != null)
      throw new UnsupportedOperationException(
          "You can only use ONE source for deletion, or an IQuery or a list of instances");
    recordList.add(record);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#add(java.lang.Object[])
   */
  @SuppressWarnings("unchecked")
  @Override
  public void add(T... records) {
    if (query != null)
      throw new UnsupportedOperationException(
          "You can only use ONE source for deletion, or an IQuery or a list of instances");
    recordList.addAll(Arrays.asList(records));
  }

  /**
   * @return the query
   */
  protected IQuery<T> getQuery() {
    return query;
  }

  /**
   * @return the recordList
   */
  protected List<T> getRecordList() {
    return recordList;
  }

}
