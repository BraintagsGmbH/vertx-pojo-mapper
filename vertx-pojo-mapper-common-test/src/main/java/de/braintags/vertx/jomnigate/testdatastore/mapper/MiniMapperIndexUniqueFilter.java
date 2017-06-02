/*-
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
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.IndexOptions;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;

@Entity
@Indexes(@Index(name = "testindexMiniMapperIndexPartialFilter", fields = {
    @IndexField(fieldName = "name") }, options = @IndexOptions(unique = true, partialFilterExpression = "{\"condition\":{\"field\":\"filter\",\"operator\":\"EQUALS\",\"value\":\"filtered\"}}")))
public class MiniMapperIndexUniqueFilter {

  @Id
  public String id = null;
  public String name = "testName";
  public String filter = "no-filter";

  public transient String transientString;

  public MiniMapperIndexUniqueFilter(boolean filter) {
    this.filter = (filter ? "filtered" : "no-filter");
  }
}
