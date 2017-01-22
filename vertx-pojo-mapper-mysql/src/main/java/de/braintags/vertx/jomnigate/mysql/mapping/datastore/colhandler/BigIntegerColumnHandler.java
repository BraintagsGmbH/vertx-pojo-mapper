/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler;

import java.math.BigInteger;

import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo;

/**
 * 
 * A columnhandler to deal {@link BigInteger}
 * 
 * @author Michael Remme
 * 
 */

public class BigIntegerColumnHandler extends StringColumnHandler {

  /**
   * handles {@link BigInteger}
   */
  public BigIntegerColumnHandler() {
    super(BigInteger.class);
  }

  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getLength() == Property.UNDEFINED_INTEGER)
      ci.setLength(128);
    if (ci.getType() == null || ci.getType().isEmpty())
      ci.setType(CHAR_TYPE);
  }

}
