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

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;
import io.vertx.core.json.JsonObject;

/**
 * Handles {@link URI} and {@link URL}
 * 
 * @author Michael Remme
 * 
 */

public class JsonColumnHandler extends StringColumnHandler {

  /**
   * Constructor
   */
  public JsonColumnHandler() {
    super(JsonObject.class);
  }

  /**
   * Constructor for a extending classes
   * 
   * @param classesToDeal
   *          the classes, which shall be handled
   */
  protected JsonColumnHandler(Class<?>... classesToDeal) {
    super(classesToDeal);
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(VARCHAR_MAX);
    super.applyMetaData(ci);
  }
}
