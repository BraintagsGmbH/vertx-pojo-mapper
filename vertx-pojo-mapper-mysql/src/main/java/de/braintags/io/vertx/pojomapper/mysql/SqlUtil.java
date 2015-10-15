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

package de.braintags.io.vertx.pojomapper.mysql;

import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;

/**
 * Utility methods for sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlUtil {
  private static final List<String> NUMBERIC_TYPES = Arrays.asList("INT", "INTEGER", "TINYINT", "DOUBLE", "BIGINT",
      "SMALLINT", "MEDIUMINT", "FLOAT", "REAL", "DECIMAL", "NUMERIC");
  private static final List<String> CHARACTER_TYPES = Arrays.asList("CHAR", "VARCHAR", "LONGTEXT", "TEXT");
  private static final List<String> DATE_TYPES = Arrays.asList("DATE", "DATETIME", "TIMESTAMP", "TIME", "YEAR");

  /**
   * 
   */
  private SqlUtil() {
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is numeric
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if numeric
   */
  public static boolean isNumeric(IColumnInfo col) {
    return NUMBERIC_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is character based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if character based
   */
  public static boolean isCharacter(IColumnInfo col) {
    return CHARACTER_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is date / time based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if date / time based
   */
  public static boolean isDateTime(IColumnInfo col) {
    return DATE_TYPES.contains(col.getType().toUpperCase());
  }

}
