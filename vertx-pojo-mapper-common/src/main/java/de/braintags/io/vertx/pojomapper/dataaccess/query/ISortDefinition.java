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
package de.braintags.io.vertx.pojomapper.dataaccess.query;

/**
 * Definition of sort criteria inside an {@link IQuery}
 * 
 * @author Michael Remme
 * 
 */
public interface ISortDefinition<T> {

  IQuery<T> parent();

  /**
   * Add a field to sort the resulting selection by. This method is the same than addSort( fieldName, true )
   * 
   * @param sortField
   *          the field, by which to sort the selection
   * @return an instance of {@link ISortDefinition} for fluent access
   */
  ISortDefinition<T> addSort(String sortField);

  /**
   * Add a field to sort the resulting selection by. This method is the same than addSort( fieldName, true )
   * 
   * @param sortField
   *          the field, by which to sort the selection
   * @param ascending
   *          true, if sort shall be ascending
   * @return an instance of {@link ISortDefinition} for fluent access
   */
  ISortDefinition<T> addSort(String sortField, boolean ascending);

  /**
   * @return true if there is no sort definition, otherwise false
   */
  boolean isEmpty();

}
