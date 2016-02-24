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
package de.braintags.io.vertx.pojomapper.impl;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultColumnInfo;

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
  public DummyColumnInfo(IField field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

}
