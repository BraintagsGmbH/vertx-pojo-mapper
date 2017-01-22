/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.EntityOption;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.IndexType;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;

@Entity(options = { @EntityOption(key = "ENGINE", value = "MyISAM") })
@Indexes(@Index(name = "testindex", fields = { @IndexField(fieldName = "position", type = IndexType.GEO2DSPHERE) }))
public class GeoMapper {
  @Id
  public String id;

  @Property(nullable = false)
  public GeoPoint position;
  public String name;

  public GeoMapper() {
  }

  public GeoMapper(GeoPoint position, String name) {
    this.position = position;
    this.name = name;
  }

}
