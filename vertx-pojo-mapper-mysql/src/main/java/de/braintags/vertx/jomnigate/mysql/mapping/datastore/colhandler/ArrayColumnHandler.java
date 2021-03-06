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
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo;

/**
 * Handles Arrays
 * 
 * @author Michael Remme
 * 
 */

public class ArrayColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public ArrayColumnHandler() {
    super();
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(VARCHAR_MAX);
    super.applyMetaData(ci);
  }

  @Override
  public short matches(IProperty field) {
    if (field.isArray())
      return MATCH_MAJOR;

    return MATCH_NONE;
  }

}
