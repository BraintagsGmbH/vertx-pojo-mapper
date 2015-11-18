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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import java.util.Set;

import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IStoreObject} for use with sql databases
 * 
 * @author Michael Remme
 * 
 */

public class SqlStoreObject extends JsonStoreObject {

  /**
   * Creates a new instance by using a POJO. This constructor is usually used, when a pojo shall be stored
   * 
   * @param mapper
   * @param entity
   */
  public SqlStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity);
  }

  /**
   * Creates a new instance by using the result of a query inside the database
   * 
   * @param rowResult
   * @param mapper
   */
  public SqlStoreObject(JsonObject rowResult, IMapper mapper) {
    super(rowResult, mapper);
  }

  /**
   * Generates the sql statement to insert a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public void generateSqlInsertStatement(Handler<AsyncResult<SqlSequence>> resultHandler) {
    try {
      ITableInfo tInfo = getMapper().getTableInfo();
      SqlSequence sequence = new SqlSequence(tInfo.getName());
      Set<String> fieldNames = getMapper().getFieldNames();
      for (String fieldName : fieldNames) {
        IField field = getMapper().getField(fieldName);
        if (field != getMapper().getIdField()) {
          sequence.addEntry(field.getColumnInfo().getName(), get(field));
        }
      }
      getNextId(tInfo, sequence, resultHandler);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  private void getNextId(ITableInfo tInfo, SqlSequence sequence, Handler<AsyncResult<SqlSequence>> resultHandler) {
    IKeyGenerator gen = this.getMapper().getKeyGenerator();
    if (gen == null) {
      throw new UnsupportedOperationException(String.format(
          "No keygenerator defined for mapper %s. Did you set the property IKeyGenerator.DEFAULT_KEY_GERNERATOR for your datastore? ",
          getMapper().getMapperClass().getName()));
    }
    Object genKey = gen.generateKey();
    IField idField = getMapper().getIdField();
    idField.getTypeHandler().intoStore(genKey, idField, thResult -> {
      if (thResult.failed()) {
        resultHandler.handle(Future.failedFuture(thResult.cause()));
      } else {
        Object idValue = thResult.result().getResult();
        sequence.addEntry(idField.getColumnInfo().getName(), idValue);
        put(idField, idValue);
        resultHandler.handle(Future.succeededFuture(sequence));
      }
    });
  }

  /**
   * Generates the sql statement to update a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlUpdateStatement() {
    ITableInfo tInfo = getMapper().getTableInfo();
    IField idField = getMapper().getIdField();
    Object id = get(idField);

    SqlSequence sequence = new SqlSequence(tInfo.getName(), idField.getColumnInfo(), id);

    Set<String> fieldNames = getMapper().getFieldNames();
    for (String fieldName : fieldNames) {
      IField field = getMapper().getField(fieldName);
      if (field != idField) {
        sequence.addEntry(tInfo.getColumnInfo(field).getName(), get(field));
      }
    }
    return sequence;
  }

  class SqlSequence {
    boolean added = false;
    private StringBuilder headStatement;
    private StringBuilder setStatement;
    private StringBuilder whereStatement;
    private Object id;
    private JsonArray parameters = new JsonArray();

    /**
     * Constructor for an insert command
     * 
     * @param tableName
     */
    public SqlSequence(String tableName) {
      headStatement = new StringBuilder("Insert into ").append(tableName);
      setStatement = new StringBuilder(" set ");
    }

    /**
     * Constructor for an update command
     * 
     * @param tableName
     *          the name of the table
     * @param idColInfo
     *          the {@link IColumnInfo} for the id column
     * @param idValue
     *          the id value
     */
    public SqlSequence(String tableName, IColumnInfo idColInfo, Object idValue) {
      headStatement = new StringBuilder("UPDATE ").append(tableName);
      setStatement = new StringBuilder(" set ");
      whereStatement = new StringBuilder(" WHERE ").append(idColInfo.getName()).append(" = ?");
      this.id = idValue;

    }

    void addEntry(String colName, Object value) {
      if (value == null)
        return;
      if (added)
        setStatement.append(", ");
      setStatement.append(colName).append(" = ?");
      parameters.add(value);
      added = true;
    }

    /**
     * Get the statement
     * 
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
      StringBuilder ret = new StringBuilder(headStatement);
      if (parameters.isEmpty())
        ret.append(" () VALUES ()");// insert into SimpleMapper () VALUES ()
      else
        ret.append(setStatement);
      if (whereStatement != null)
        ret.append(whereStatement);
      return ret.toString();
    }

    /**
     * @return the parameters
     */
    public final JsonArray getParameters() {
      if (id != null)
        return parameters.copy().add(id);
      return parameters;
    }

    @Override
    public String toString() {
      return getSqlStatement() + " | " + getParameters();
    }
  }
}
