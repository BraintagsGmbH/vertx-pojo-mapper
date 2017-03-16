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
package de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.geo;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo;

/**
 * ColumnHandler dealing with instances of {@link GeoPoint}
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointColumnHandler extends AbstractGeoColumnHandler {
  public static final String POINT_TYPE = "POINT";

  /**
   * 
   */
  public GeoPointColumnHandler() {
    super(GeoPoint.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.braintags
   * .vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo)
   */
  @Override
  public void applyMetaData(SqlColumnInfo column) {
    column.setType(POINT_TYPE);
  }

  @Override
  protected StringBuilder generateColumn(IProperty field, IColumnInfo ci) {
    return new StringBuilder(String.format("%s %s ", ci.getName(), ci.getType()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#checkColumnModified(de.
   * braintags.vertx.jomnigate.mapping.datastore.IColumnInfo,
   * de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo)
   */
  @Override
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return super.checkColumnModified(plannedCi, existingCi) && !existingCi.getType().equalsIgnoreCase(POINT_TYPE);
  }

}
