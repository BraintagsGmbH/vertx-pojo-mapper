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

import java.util.Properties;

import de.braintags.vertx.jomnigate.annotation.Entity;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class PropertiesRecord extends BaseRecord {
  public Properties properties = new Properties();

  public PropertiesRecord() {
    properties.put("Eins", "1");
    properties.put("Zwei", "2");
    properties.put("Drei", "3");
  }

}
