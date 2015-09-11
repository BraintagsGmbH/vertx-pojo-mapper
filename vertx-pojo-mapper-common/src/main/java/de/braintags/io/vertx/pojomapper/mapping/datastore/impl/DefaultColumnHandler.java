/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.datastore.impl;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;

/**
 * Used as default handler in the {@link DefaultTableGenerator}
 * 
 * @author Michael Remme
 * 
 */

public class DefaultColumnHandler implements IColumnHandler {

  /**
   * 
   */
  public DefaultColumnHandler() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler#matches(de.braintags.io.vertx.pojomapper.mapping
   * .IField)
   */
  @Override
  public short matches(IField field) {
    return IColumnHandler.MATCH_NONE;
  }

}
