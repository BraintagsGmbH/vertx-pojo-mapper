/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.testdatastore.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.Index;
import de.braintags.io.vertx.pojomapper.annotation.IndexField;
import de.braintags.io.vertx.pojomapper.annotation.IndexType;
import de.braintags.io.vertx.pojomapper.annotation.Indexes;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;

@Entity
@Indexes(@Index(name = "testindex", fields = { @IndexField(fieldName = "position", type = IndexType.GEO2DSPHERE) }))
public class GeoMapper {
  @Id
  public String id;
  public GeoPoint position;
  public String name;

  public GeoMapper() {
  }

  public GeoMapper(GeoPoint position, String name) {
    this.position = position;
    this.name = name;
  }

}
