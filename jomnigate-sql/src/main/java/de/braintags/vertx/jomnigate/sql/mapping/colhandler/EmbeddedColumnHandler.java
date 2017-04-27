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

package de.braintags.vertx.jomnigate.sql.mapping.colhandler;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.sql.mapping.SqlColumnInfo;

/**
 * Handles embedded objects, which are stored as Json String into a text field
 * 
 * @author Michael Remme
 * 
 */

public class EmbeddedColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public EmbeddedColumnHandler() {
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
    if (field.hasAnnotation(Embedded.class))
      return MATCH_MAJOR;
    return MATCH_NONE;
  }

}
