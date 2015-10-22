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

import java.net.URI;
import java.net.URL;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MiscMapper extends BaseRecord {
  public char charValue = 'a';
  public Character character = new Character('c');
  public URI uri;
  public URL url;
  public Class myClass = StringBuffer.class;

}
