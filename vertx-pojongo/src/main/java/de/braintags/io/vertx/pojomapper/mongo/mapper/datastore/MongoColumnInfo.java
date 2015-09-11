/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.mapper.datastore;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultColumnInfo;

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
  public MongoColumnInfo(IField field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

  @Override
  protected String computePropertyName(IField field) {
    if (field.hasAnnotation(Id.class)) {
      return ID_FIELD_NAME;
    }
    return super.computePropertyName(field);
  }

}
