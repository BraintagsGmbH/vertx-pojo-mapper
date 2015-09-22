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

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * IColumn keeps information about the structure of the column inside the connected datastore for an {@link IField}. If
 * the column is existing already inside the connected datastore, the information in here are filled by the existing
 * column. If it doesn't exist yet, the information are created by a defined {@link Property} annotation
 * 
 * @author Michael Remme
 * 
 */

public interface IColumnInfo {

  /**
   * Get the name of the column inside the datastore
   * 
   * @return the name of the column
   */
  public String getName();

  /**
   * Get the IColumnHandler which is used by the current column info
   * 
   * @return the columnhandler
   */
  public IColumnHandler getColumnHandler();

  /**
   * Returns the type of the column
   * 
   * @return the type as String or {@link Property#UNDEFINED_COLUMN_TYPE}
   */
  public String getType();

  /**
   * Get the length of the column
   * 
   * @return the length or {@link Property#UNDEFINED_INTEGER}
   */
  public int getLength();

  /**
   * Get the scale of the column
   * 
   * @return the scale or {@link Property#UNDEFINED_INTEGER}
   */
  public int getScale();

  /**
   * Get the precision of the column
   * 
   * @return the precision or {@link Property#UNDEFINED_INTEGER}
   */
  public int getPrecision();

  /**
   * Get the information if a column can be null or not
   * 
   * @return true, if null is allowed
   */
  public boolean isNullable();

  /**
   * DEfines, wether a column can have only unique values
   * 
   * @return true, if only unique values are allowed
   */
  public boolean isUnique();

}
