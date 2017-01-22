/*-
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
package de.braintags.vertx.jomnigate.mysql.mapping;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoJsonObject;
import de.braintags.vertx.jomnigate.mapping.impl.MappedField;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapper<T> extends Mapper<T> {
  private boolean queryWithFieldNames = false;
  private String queryFieldList = null;

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public SqlMapper(Class<T> mapperClass, MapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
  }

  /**
   * Get the sequence of fieldnames for a query
   * If there are existing fields as GeoJson, a query sequence is returned, where not the wildcard is used but the
   * single fields
   * 
   * @return
   */
  public String getQueryFieldNames() {
    if (queryFieldList == null) {
      initFieldList();
    }
    return queryWithFieldNames ? queryFieldList : "*";
  }

  private void initFieldList() {
    getFieldNames().forEach(name -> addFieldEntry(name));
  }

  private void addFieldEntry(String name) {
    MappedField mf = (MappedField) getField(name);
    if (queryFieldList != null) {
      queryFieldList += ", ";
    } else {
      queryFieldList = "";
    }
    if (GeoJsonObject.class.isAssignableFrom(mf.getType())) {
      queryWithFieldNames = true;
      queryFieldList = queryFieldList + "AsText( " + mf.getColumnInfo().getName() + " ) AS " + mf.getColumnInfo().getName();
    } else {
      queryFieldList += mf.getColumnInfo().getName();
    }
  }

}
