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

package de.braintags.io.vertx.pojomapper.mapping.datastore.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultTableInfo implements ITableInfo {
  private String name;
  private Map<String, IColumnInfo> cols = new HashMap<String, IColumnInfo>();

  /**
   * 
   */
  public DefaultTableInfo(IMapper mapper) {
    if (mapper.getEntity() == null)
      this.name = mapper.getMapperClass().getSimpleName();
    else
      this.name = mapper.getEntity().name();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo#createColumn(de.braintags.io.vertx.pojomapper.mapping
   * .IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler)
   */
  @Override
  public void createColumnInfo(IField field, IColumnHandler columnHandler) {
    String fieldName = field.getName();
    DefaultColumnInfo ci = new DefaultColumnInfo(field, columnHandler);
    addColumnInfo(fieldName, ci);
  }

  protected void addColumnInfo(String fieldName, IColumnInfo ci) {
    cols.put(fieldName, ci);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo#getColumnInfo(java.lang.String)
   */
  @Override
  public IColumnInfo getColumnInfo(IField field) {
    return cols.get(field.getName());
  }

}
