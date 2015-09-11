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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.AbstractColumnHandler;

/**
 * 
 * @author Michael Remme
 * 
 */

public class StringColumnHandler extends AbstractColumnHandler {

  /**
   * 
   */
  public StringColumnHandler() {
    super(CharSequence.class);
  }

  @Override
  public Object generate(IField field) {
    throw new UnsupportedOperationException();
  }

}
