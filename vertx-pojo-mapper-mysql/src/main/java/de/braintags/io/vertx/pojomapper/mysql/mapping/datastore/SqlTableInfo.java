/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo;

/**
 * An implementation of {@link ITableInfo} for use with sql based datastores
 * 
 * @author Michael Remme
 * 
 */

public class SqlTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public SqlTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo#createColumnInfo(de.braintags.io.vertx.
   * pojomapper.mapping.IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler)
   */
  @Override
  public void createColumnInfo(IField field, IColumnHandler columnHandler) {
    String fieldName = field.getName();
    SqlColumnInfo ci = new SqlColumnInfo(field, columnHandler);
    addColumnInfo(fieldName, ci);
  }

}
