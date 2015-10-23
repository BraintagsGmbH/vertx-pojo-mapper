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

import java.util.Arrays;
import java.util.List;

/**
 * Mapper with a JsonObject
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedListMapper extends BaseRecord {
  public List arrayList = Arrays.asList("Eins", "Zwei", "drei"); // no subtype defined
  public List mixedList = Arrays.asList("Eins", "Zwei", 5, "vier", new Long(99994444)); // no subtype defined

}
