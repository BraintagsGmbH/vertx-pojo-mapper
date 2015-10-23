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

import java.net.URI;
import java.net.URL;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * Handles {@link URI} and {@link URL}
 * 
 * @author Michael Remme
 * 
 */

public class ListColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public ListColumnHandler() {
    super(List.class);
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(VARCHAR_MAX);
    super.applyMetaData(ci);
  }
}
