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
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultColumnInfo;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DummyColumnInfo extends DefaultColumnInfo {

  /**
   * @param colName
   */
  public DummyColumnInfo(String colName) {
    super(colName);
  }

  /**
   * @param field
   * @param columnHandler
   */
  public DummyColumnInfo(IProperty field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

}
