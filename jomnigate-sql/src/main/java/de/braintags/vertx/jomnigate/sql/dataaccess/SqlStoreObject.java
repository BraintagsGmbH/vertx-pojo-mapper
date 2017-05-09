/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.sql.dataaccess;

import java.util.Set;

import de.braintags.vertx.jomnigate.json.dataaccess.JsonStoreObject;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IStoreObject} for use with sql databases
 * 
 * @uthor Michael Remme
 * @param <T>
 *          the type of the entity
 * 
 */

public class SqlStoreObject<T> extends JsonStoreObject<T> {

  /**
   * @param mapper
   * @param entity
   */
  public SqlStoreObject(IMapper<T> mapper, T entity) {
    super(mapper, entity);
  }

  /**
   * @param jsonObject
   * @param mapper
   */
  public SqlStoreObject(JsonObject jsonObject, IMapper<T> mapper) {
    super(jsonObject, mapper);
  }

  /**
   * Generates the sql statement to insert a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlInsertStatement() {
    ITableInfo tInfo = getMapper().getTableInfo();
    SqlSequence sequence = new SqlSequence(tInfo.getName());
    Set<String> fieldNames = getMapper().getFieldNames();
    for (String fieldName : fieldNames) {
      IProperty field = getMapper().getField(fieldName);
      sequence.addEntry(field.getColumnInfo().getName(), get(field));
    }
    return sequence;
  }

  /**
   * Generates the sql statement to update a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlUpdateStatement() {
    ITableInfo tInfo = getMapper().getTableInfo();
    IProperty idField = getMapper().getIdInfo().getField();
    Object id = get(idField);

    SqlSequence sequence = new SqlSequence(tInfo.getName(), idField.getColumnInfo(), id);

    Set<String> fieldNames = getMapper().getFieldNames();
    for (String fieldName : fieldNames) {
      IProperty field = getMapper().getField(fieldName);
      if (field != idField) {
        sequence.addEntry(tInfo.getColumnInfo(field).getName(), get(field));
      }
    }
    return sequence;
  }
}
