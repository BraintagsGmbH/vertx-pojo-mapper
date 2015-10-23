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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;

/**
 * Mapper with a JsonObject
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedListMapper extends BaseRecord {
  @SuppressWarnings("rawtypes")
  public List arrayList = Arrays.asList("Eins", "Zwei", "drei"); // no subtype defined
  @SuppressWarnings("rawtypes")
  public List mixedList = Arrays.asList("Eins", "Zwei", 5, "vier", new Long(99994444)); // no subtype defined

  public List<Date> dateList = new ArrayList<Date>();

  @Embedded
  public List<StringTestMapper> stringTestList = new ArrayList<>();

  public EmbeddedListMapper() {
    dateList.add(new Date());

    stringTestList.add(new StringTestMapper(1));
    stringTestList.add(new StringTestMapper(2));
  }

}
