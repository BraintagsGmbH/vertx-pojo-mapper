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

import java.util.Properties;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class PropertiesRecord extends BaseRecord {
  @Embedded
  public Properties properties = new Properties();

  public PropertiesRecord() {
    properties.put("Eins", 1);
    properties.put("Zwei", 2);
    properties.put("Drei", 3);
  }

}
