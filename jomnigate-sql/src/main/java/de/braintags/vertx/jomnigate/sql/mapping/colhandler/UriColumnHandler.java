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

import java.net.URI;
import java.net.URL;

import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.sql.mapping.SqlColumnInfo;

/**
 * Handles {@link URI} and {@link URL}
 * 
 * @author Michael Remme
 * 
 */

public class UriColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public UriColumnHandler() {
    super(URI.class, URL.class);
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(1024);
    super.applyMetaData(ci);
  }
}
