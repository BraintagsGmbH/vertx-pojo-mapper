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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableGenerator;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.ArrayColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.BigDecimalColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.BigIntegerColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.BooleanColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.ByteColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.CharColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.ClassColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.CollectionColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.DateColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.DoubleColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.EmbeddedColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.EnumColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.IntegerColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.JsonColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.LocaleColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.LongColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.MapColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.PriceColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.ReferencedColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.ShortColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.StringColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.UriColumnHandler;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.geo.GeoPointColumnHandler;

/**
 * An implementation for SQL datastores
 * 
 * @author Michael Remme
 * 
 */

public class SqlTableGenerator extends DefaultTableGenerator {

  static {
    definedColumnHandlers.add(new StringColumnHandler());
    definedColumnHandlers.add(new ByteColumnHandler());
    definedColumnHandlers.add(new DoubleColumnHandler());
    definedColumnHandlers.add(new IntegerColumnHandler());
    definedColumnHandlers.add(new LongColumnHandler());
    definedColumnHandlers.add(new ShortColumnHandler());
    definedColumnHandlers.add(new BigIntegerColumnHandler());
    definedColumnHandlers.add(new BigDecimalColumnHandler());
    definedColumnHandlers.add(new PriceColumnHandler());
    definedColumnHandlers.add(new BooleanColumnHandler());
    definedColumnHandlers.add(new DateColumnHandler());
    definedColumnHandlers.add(new CharColumnHandler());
    definedColumnHandlers.add(new ClassColumnHandler());
    definedColumnHandlers.add(new UriColumnHandler());
    definedColumnHandlers.add(new JsonColumnHandler());
    definedColumnHandlers.add(new GeoPointColumnHandler());
    definedColumnHandlers.add(new CollectionColumnHandler());
    definedColumnHandlers.add(new MapColumnHandler());
    definedColumnHandlers.add(new ArrayColumnHandler());
    definedColumnHandlers.add(new EmbeddedColumnHandler());
    definedColumnHandlers.add(new EnumColumnHandler());
    definedColumnHandlers.add(new LocaleColumnHandler());
    definedColumnHandlers.add(new ReferencedColumnHandler());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableGenerator#createTableInfo(de.braintags.io.vertx
   * .util
   * .pojomapper.mapping.IMapper)
   */
  @Override
  public ITableInfo createTableInfo(IMapper mapper) {
    return new SqlTableInfo(mapper);
  }

  /**
   * The sql implementation does not allow NULL as return value here
   */
  @Override
  public IColumnHandler getColumnHandler(IField field) {
    IColumnHandler handler = super.getColumnHandler(field);
    if (handler == null)
      throw new MappingException("Could not identfy a valid ColumnHandler for field " + field.getFullName());
    return handler;
  }
}
