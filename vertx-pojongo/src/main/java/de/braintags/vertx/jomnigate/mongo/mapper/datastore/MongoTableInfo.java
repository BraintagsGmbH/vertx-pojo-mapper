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

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableInfo;

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
   * de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.vertx.util.
   * pojomapper.mapping.IField, de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IField field, IColumnHandler columnHandler) {
    return new MongoColumnInfo(field, columnHandler);
  }

}
