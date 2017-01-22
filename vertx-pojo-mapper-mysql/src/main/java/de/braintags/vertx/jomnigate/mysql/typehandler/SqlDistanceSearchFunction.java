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

package de.braintags.vertx.jomnigate.mysql.typehandler;

import de.braintags.vertx.jomnigate.dataaccess.query.impl.GeoSearchArgument;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.mapping.IField;

/**
 * An SqlFunction can be used as return type by TypeHandlers as value. This function is used as return type, when
 * a {@link GeoSearchArgument} is used ( which comes for a query NEAR ) and creates the sequence ST_DISTANCE_SPHERE like
 * shown below:
 * 
 * select * from GeoMapper where ST_DISTANCE_SPHERE( position, ST_GeomFromText( 'POINT(6.775763 51.224906)' ) ) <=
 * 40000;
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlDistanceSearchFunction {
  private String functionSequence;
  private GeoSearchArgument source;

  public SqlDistanceSearchFunction(GeoSearchArgument source, IField field) {
    this.source = source;
    GeoPoint searchPoint = (GeoPoint) source.getGeoJson();
    String longitude = searchPoint.getCoordinates().getValues().get(0).toString();
    String latitude = searchPoint.getCoordinates().getValues().get(1).toString();
    functionSequence = "ST_DISTANCE_SPHERE( " + field.getColumnInfo().getName() + ", ST_GeomFromText( 'POINT("
        + longitude + " " + latitude + " )' ) ) <= ?";

  }

  /**
   * The complete sequence of the function
   * 
   * @return the functionName
   */
  public String getFunctionSequence() {
    return functionSequence;
  }

  /**
   * The content of the function to be used
   * 
   * @return the content
   */
  public Object getValue() {
    return String.valueOf(source.getDistance());
  }

}
