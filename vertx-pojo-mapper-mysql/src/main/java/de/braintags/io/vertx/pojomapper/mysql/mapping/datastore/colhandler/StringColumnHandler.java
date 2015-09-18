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

/**
 * 
 * @author Michael Remme
 * 
 */

public class StringColumnHandler extends AbstractSqlColumnHandler {

  private static final int CHAR_MAX = 50;
  private int VARCHAR_MAX = 32000;

  /**
   * 
   */
  public StringColumnHandler() {
    super(CharSequence.class);
  }

  @Override
  protected StringBuilder generateColumn(IField field, Property prop) {
    StringBuilder result = new StringBuilder();
    int length = prop == null ? 255 : prop.length();
    if (length < CHAR_MAX)
      generateChar(result, field, length);
    else if (length < VARCHAR_MAX)
      generateVarchar(result, field, length);
    else
      generateText(result, field, length);
    return result;
  }

  private void generateChar(StringBuilder result, IField field, int length) {
    String.format("%s CHAR( %d ) ", field.getColumnInfo().getName(), length);
  }

  private void generateVarchar(StringBuilder result, IField field, int length) {
    String.format("%s VARCHAR( %d ) ", field.getColumnInfo().getName(), length);
  }

  private void generateText(StringBuilder result, IField field, int length) {
    String.format("%s LONGTEXT ", field.getColumnInfo().getName(), length);
  }
}
