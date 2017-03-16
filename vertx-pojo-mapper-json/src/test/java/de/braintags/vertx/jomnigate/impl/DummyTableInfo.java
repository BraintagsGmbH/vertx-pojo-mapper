/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.impl;

import de.braintags.vertx.jomnigate.mapping.IProperty;
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
public class DummyTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public DummyTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.vertx.
   * pojomapper.mapping.IField, de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IProperty field, IColumnHandler columnHandler) {
    return new DummyColumnInfo(field, columnHandler);
  }

}
