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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableGenerator;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.StringColumnHandler;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlTableGenerator extends DefaultTableGenerator {

  static {
    definedColumnHandlers.add(new StringColumnHandler());
  }

  /**
   * 
   */
  public SqlTableGenerator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableGenerator#getDefaultColumnHandler()
   */
  @Override
  public IColumnHandler getDefaultColumnHandler() {
    return super.getDefaultColumnHandler();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableGenerator#createTableInfo(de.braintags.io.vertx
   * .pojomapper.mapping.IMapper)
   */
  @Override
  public ITableInfo createTableInfo(IMapper mapper) {
    return new SqlTableInfo(mapper);
  }

}
