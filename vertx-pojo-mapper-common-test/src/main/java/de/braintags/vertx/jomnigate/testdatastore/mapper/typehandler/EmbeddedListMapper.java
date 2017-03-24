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
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import java.util.ArrayList;
import java.util.Collection;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;

/**
 * Mapper with a JsonObject
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class EmbeddedListMapper extends BaseRecord {

  @Embedded
  public Collection<StringTestMapper> stringTestList = new ArrayList<>();

  @Embedded
  public Collection<DateMapper> dateTestList = new ArrayList<>();

  public EmbeddedListMapper() {
    // dateList.add(new Date());

    stringTestList.add(new StringTestMapper(1));
    stringTestList.add(new StringTestMapper(2));

    dateTestList.add(new DateMapper());
  }

}
