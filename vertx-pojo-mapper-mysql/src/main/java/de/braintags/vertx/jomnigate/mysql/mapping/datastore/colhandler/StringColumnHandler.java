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

package de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler;

import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo;

/**
 * StringColumnHandler is used to generate columns which are character based
 * 
 * @author Michael Remme
 * 
 */

public class StringColumnHandler extends AbstractSqlColumnHandler {
  private static final int DEFAULT_LENGTH = 255;

  public static final int CHAR_MAX = 50;
  public static final int VARCHAR_MAX = 32000;

  public static final String CHAR_TYPE = "char";
  public static final String VARCHAR_TYPE = "varchar";
  public static final String LONGTEXT_TYPE = "longtext";

  /**
   * Constructor for a StringColumnHandler
   */
  public StringColumnHandler() {
    super(CharSequence.class);
  }

  /**
   * Constructor for a extending classes
   * 
   * @param classesToDeal
   *          the classes, which shall be handled
   */
  protected StringColumnHandler(Class<?>... classesToDeal) {
    super(classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.
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
      return CHAR_TYPE;
    else if (length < VARCHAR_MAX)
      return VARCHAR_TYPE;
    else
      return LONGTEXT_TYPE;

  }

  @Override
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    String plannedType = plannedCi.getType();
    boolean ret = !(plannedType.equalsIgnoreCase(CHAR_TYPE) || plannedType.equalsIgnoreCase(VARCHAR_TYPE)
        || plannedType.equalsIgnoreCase(LONGTEXT_TYPE));
    return ret;
  }

  @Override
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
    StringBuilder result = new StringBuilder();
    if (ci.getType().equalsIgnoreCase(CHAR_TYPE) || ci.getType().equals(VARCHAR_TYPE))
      generateChar(result, ci);
    else if (ci.getType().equalsIgnoreCase(LONGTEXT_TYPE))
      generateText(result, ci);
    else
      throw new UnsupportedOperationException(String.format("Undefined type: %s", ci.getType()));
    return result;
  }

  private void generateChar(StringBuilder result, IColumnInfo ci) {
    result.append(String.format("%s %s( %d ) ", ci.getName(), ci.getType(), ci.getLength()));
  }

  private void generateText(StringBuilder result, IColumnInfo ci) {
    result.append(String.format("%s %s ", ci.getName(), ci.getType()));
  }

}
