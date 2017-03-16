/*-
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mongo.mapper.datastore;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultColumnInfo;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoColumnInfo extends DefaultColumnInfo {
  private static final String ID_FIELD_NAME = "_id";

  /**
   * @param field
   * @param columnHandler
   */
  public MongoColumnInfo(IProperty field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

  @Override
  protected String computePropertyName(IProperty field) {
    if (field.hasAnnotation(Id.class)) {
      return ID_FIELD_NAME;
    }
    return super.computePropertyName(field);
  }

}
