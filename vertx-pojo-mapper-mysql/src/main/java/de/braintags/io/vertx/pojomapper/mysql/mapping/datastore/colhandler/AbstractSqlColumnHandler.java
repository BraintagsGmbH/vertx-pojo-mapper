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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.AbstractColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * An abstract implementation for use with SQL based datastores The implementation checks, wether
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractSqlColumnHandler extends AbstractColumnHandler {
  private static final String ID_COLUMN_STRING = "%s %s (%d) NOT NULL auto_increment";

  /**
   * @param classesToDeal
   */
  public AbstractSqlColumnHandler(Class<?>... classesToDeal) {
    super(classesToDeal);
  }

  @Override
  public final Object generate(IField field) {
    IColumnInfo ci = field.getColumnInfo();
    if (field.getMapper().getIdField() == field) {
      return generateIdColumn(field, ci);
    } else {
      StringBuilder colString = generateColumn(field, ci);
      addNotNull(colString, ci);
      return colString.toString();
    }
  }

  /**
   * This method generates the metadata like type, length etc., if they are not defined already by annotations
   * 
   * @param ci
   */
  public final void applyMetaData(IField field, SqlColumnInfo ci) {
    if (ci.isId()) {
      applyIdMetaData(field, ci);
    } else {
      applyMetaData(ci);
    }
  }

  /**
   * Applies the meta data for the field, which is specified as {@link Id}
   * 
   * @param field
   *          the underlaying field
   * @param ci
   *          the {@link SqlColumnInfo}
   */
  protected void applyIdMetaData(IField field, SqlColumnInfo ci) {
    ci.setType("int");
    ci.setScale(10);
  }

  /**
   * Apply the metadata for columns
   * 
   * @param column
   *          the {@link SqlColumnInfo} to handle
   */
  public abstract void applyMetaData(SqlColumnInfo column);

  protected void addNotNull(StringBuilder colString, IColumnInfo ci) {
    if (!ci.isNullable())
      colString.append(" NOT NULL");
  }

  /**
   * Generates a sequence like "id int(10) NOT NULL auto_increment"
   * 
   * @param field
   * @return
   */
  protected String generateIdColumn(IField field, IColumnInfo ci) {
    String propName = ci.getName();
    int scale = ci.getScale();
    scale = scale == Property.UNDEFINED_INTEGER ? 10 : scale;
    return String.format(ID_COLUMN_STRING, propName, ci.getType(), scale);
  }

  @Override
  public final boolean isColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    if (plannedCi.isId()) {
      return checkIdColumnModified(plannedCi, existingCi);
    } else {
      return checkColumnModified(plannedCi, existingCi);
    }
  }

  /**
   * The implementation checks wether the type of columns changed
   * 
   * @param plannedCi
   *          the planned {@link IColumnInfo} from out of the {@link IMapper}
   * @param existingCi
   *          the existing {@link IColumnInfo} read from the datastore
   * @return true, if structure changed
   */
  protected boolean checkIdColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return (!plannedCi.getType().equals(existingCi.getType()));
  }

  /**
   * The implementation checks wether the type of columns changed
   * 
   * @param plannedCi
   *          the planned {@link IColumnInfo} from out of the {@link IMapper}
   * @param existingCi
   *          the existing {@link IColumnInfo} read from the datastore
   * @return true, if structure changed
   */
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return (!plannedCi.getType().equalsIgnoreCase(existingCi.getType()));
  }

  /**
   * Generate the sequence to build a column inside the datastore
   * 
   * @param field
   * @return
   */
  protected abstract StringBuilder generateColumn(IField field, IColumnInfo ci);
}
