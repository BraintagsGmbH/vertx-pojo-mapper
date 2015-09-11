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

/**
 * IColumn keeps information about the structure of the column inside the connected datastore for an {@link IField}
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

}
