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

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * The ITableGenerator is a factory to create instances of {@link ITableInfo}
 * 
 * @author Michael Remme
 * 
 */

public interface ITableGenerator {

  /**
   * Generates an instance of {@link ITableInfo} from the given {@link IMapper} which is convenient for the connected
   * datastore
   * 
   * @param mapper
   *          the mapper to be handled
   * @return an instance of {@link ITableInfo}
   */
  ITableInfo createTableInfo(IMapper mapper);

  /**
   * Creates an instance of {@link IColumnHandler} which is suitable for the given {@link IField} and the connected
   * datastore
   * 
   * @param field
   *          the field to be handled
   * @return an instance of {@link IColumnHandler}
   */
  IColumnHandler getColumnHandler(IField field);

}
