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

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * 
 * @author Michael Remme
 * 
 */

public class StringColumnHandler extends AbstractSqlColumnHandler {
  private static final int DEFAULT_LENGTH = 255;

  private static final int CHAR_MAX = 50;
  private int VARCHAR_MAX = 32000;

  /**
   * 
   */
  public StringColumnHandler() {
    super(CharSequence.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.
   * braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo)
   */
  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(DEFAULT_LENGTH);
    if (ci.getType() == null || ci.getType().isEmpty())
      ci.setType(generateType(ci));

  }

  private String generateType(IColumnInfo ci) {
    int length = ci.getLength();
    if (length < CHAR_MAX)
      return "char";
    else if (length < VARCHAR_MAX)
      return "varchar";
    else
      return "longtext";

  }

  @Override
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
    StringBuilder result = new StringBuilder();
    int length = ci.getLength();
    if (length < CHAR_MAX)
      generateChar(result, ci, length);
    else if (length < VARCHAR_MAX)
      generateVarchar(result, ci, length);
    else
      generateText(result, ci, length);
    return result;
  }

  private void generateChar(StringBuilder result, IColumnInfo ci, int length) {
    result.append(String.format("%s %s( %d ) ", ci.getName(), ci.getType(), length));
  }

  private void generateVarchar(StringBuilder result, IColumnInfo ci, int length) {
    result.append(String.format("%s %s( %d ) ", ci.getName(), ci.getType(), length));
  }

  private void generateText(StringBuilder result, IColumnInfo ci, int length) {
    result.append(String.format("%s %s ", ci.getName(), ci.getType(), length));
  }

  @Override
  public boolean isColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    if (!plannedCi.getType().equals(existingCi.getType()))
      return true;
    return false;
  }

}
