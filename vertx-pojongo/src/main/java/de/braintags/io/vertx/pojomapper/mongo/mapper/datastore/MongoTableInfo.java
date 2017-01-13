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

package de.braintags.io.vertx.pojomapper.mongo.mapper.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public MongoTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.io.vertx.util.
   * pojomapper.mapping.IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IField field, IColumnHandler columnHandler) {
    return new MongoColumnInfo(field, columnHandler);
  }

}
