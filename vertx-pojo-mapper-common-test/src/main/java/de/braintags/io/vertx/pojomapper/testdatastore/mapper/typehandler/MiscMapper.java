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
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import de.braintags.io.vertx.pojomapper.annotation.Entity;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class MiscMapper extends BaseRecord {
  public char charValue = 'a';
  public Character myCharacter = new Character('c');
  public URI uri = URI.create("http://www.braintags.de");
  public URL url;
  public Class myClass = StringBuffer.class;

  public MiscMapper() {
    try {
      url = new URL("http://www.brainags.de/subsite");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

}
