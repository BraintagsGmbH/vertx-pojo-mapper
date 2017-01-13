/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;

/**
 * Implementation of {@link ISortDefinition}
 * 
 * @author Michael Remme
 * @param <T>
 *          the parent container
 * 
 */
public class SortDefinition<T> implements ISortDefinition<T> {
  private List<SortArgument> sortArgs = new ArrayList<>();
  private IQuery<T> parent;

  /**
   * 
   */
  public SortDefinition(IQuery<T> parent) {
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition#parent()
   */
  @Override
  public IQuery<T> parent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition#addSort(java.lang.String)
   */
  @Override
  public ISortDefinition<T> addSort(String sortField) {
    return addSort(sortField, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition#addSort(java.lang.String, boolean)
   */
  @Override
  public ISortDefinition<T> addSort(String sortField, boolean ascending) {
    sortArgs.add(new SortArgument(sortField, ascending));
    return this;
  }

  @Override
  public String toString() {
    StringBuffer ret = new StringBuffer();
    sortArgs.forEach(sa -> ret.append(sa.toString()).append(", "));
    return ret.toString();
  }

  /**
   * Get the list of defined sort arguments
   * 
   * @return the sortArgs
   */
  public final List<SortArgument> getSortArguments() {
    return sortArgs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return getSortArguments().isEmpty();
  }

  public class SortArgument {
    public String fieldName;
    public boolean ascending;

    SortArgument(String fieldName, boolean ascending) {
      this.fieldName = fieldName;
      this.ascending = ascending;
    }

    @Override
    public String toString() {
      return fieldName + (ascending ? " asc" : " desc");
    }

  }

}
