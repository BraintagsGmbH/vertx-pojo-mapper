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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import java.util.Map;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * Handles {@link Map}
 * 
 * @author Michael Remme
 * 
 */

public class MapColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public MapColumnHandler() {
    super(Map.class);
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(VARCHAR_MAX);
    super.applyMetaData(ci);
  }
}
