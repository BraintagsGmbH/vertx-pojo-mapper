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

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * Handles referenced objects, where the ids are stored inside a Json array as String into the field
 * 
 * @author Michael Remme
 * 
 */

public class ReferencedColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public ReferencedColumnHandler() {
    super();
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(1024);
    super.applyMetaData(ci);
  }

  @Override
  public short matches(IField field) {
    if (field.hasAnnotation(Referenced.class))
      return MATCH_MAJOR;
    return MATCH_NONE;
  }

}
