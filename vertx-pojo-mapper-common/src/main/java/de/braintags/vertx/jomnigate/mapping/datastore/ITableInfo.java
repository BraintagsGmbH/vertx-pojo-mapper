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

package de.braintags.vertx.jomnigate.mapping.datastore;

import java.util.List;

import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * ITableInfo keeps information about the structure of the connected table for an {@link IMapper}
 * 
 * @author Michael Remme
 * 
 */

public interface ITableInfo {

  /**
   * Get the name of the table inside the datastore
   * 
   * @return
   */
  public String getName();

  /**
   * Generate an instance of {@link IColumnInfo} from the information inside the given {@link IProperty}
   * 
   * @param field
   *          the field to be used to create an {@link IColumnInfo}
   * @param columnHandler
   *          the {@link IColumnHandler} to be used by the current column
   */
  public void createColumnInfo(IProperty field, IColumnHandler columnHandler);

  /**
   * Get the instance of IColumnInfo for the given {@link IProperty}
   * 
   * @param field
   *          the field to get the {@link IColumnInfo} for
   * @return an instance of IColumnInfo or null, if none existing
   */
  public IColumnInfo getColumnInfo(IProperty field);

  /**
   * Get the instance of {@link IColumnInfo} by the name of the column inside the datastore
   * 
   * @param columnName
   *          the name of the column in the datastore
   * @return an instance of IColumnInfo or null, if none existing
   */
  public IColumnInfo getColumnInfo(String columnName);

  /**
   * Get a list of all defined column names
   * 
   * @return
   */
  public List<String> getColumnNames();

}
