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

package de.braintags.io.vertx.pojomapper.mapping.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * ITable keeps information about the structure of the connected table for an {@link IMapper}
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
   * Generate an instance of {@link IColumnInfo} from the information inside the given {@link IField}
   * 
   * @param field
   *          the field to be used to create an {@link IColumnInfo}
   * @param columnHandler
   *          the {@link IColumnHandler} to be used by the current column
   */
  public void createColumnInfo(IField field, IColumnHandler columnHandler);

  /**
   * Get the instance of IColumnInfo for the given field name. NOTE: the name given here is the name of the
   * {@link IField}
   * 
   * @param javaFieldName
   *          the name of the field to be searched
   * @return an instance of IColumnInfo or null, if none existing
   */
  public IColumnInfo getColumnInfo(String javaFieldName);

}
