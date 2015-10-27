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
package de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler;

import java.util.ArrayList;
import java.util.Collection;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;

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
