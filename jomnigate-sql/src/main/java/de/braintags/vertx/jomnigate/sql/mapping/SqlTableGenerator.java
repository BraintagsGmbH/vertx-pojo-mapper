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

package de.braintags.vertx.jomnigate.sql.mapping;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableGenerator;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.ArrayColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.BigDecimalColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.BigIntegerColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.BooleanColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.ByteColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.CharColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.ClassColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.CollectionColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.DateColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.DoubleColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.EmbeddedColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.EnumColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.IntegerColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.JsonColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.LocaleColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.LongColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.MapColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.PriceColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.ReferencedColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.ShortColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.StringColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.UriColumnHandler;
import de.braintags.vertx.jomnigate.sql.mapping.colhandler.geo.GeoPointColumnHandler;

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
   * de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableGenerator#createTableInfo(de.braintags.vertx
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
  public IColumnHandler getColumnHandler(IProperty field) {
    IColumnHandler handler = super.getColumnHandler(field);
    if (handler == null)
      throw new MappingException("Could not identfy a valid ColumnHandler for field " + field.getFullName());
    return handler;
  }
}
