/*
 * Copyright 2014 Red Hat, Inc.
 * 
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

package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import java.math.BigInteger;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class BigIntegerTypeHandler extends AbstractTypeHandler {

  /**
   * @param classesToDeal
   */
  public BigIntegerTypeHandler() {
    super(BigInteger.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object fromStore(Object source, IField field, Class<?> cls) {
    return source == null ? source : new BigInteger((String) source);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object intoStore(Object source, IField field) {
    return source == null ? source : ((BigInteger) source).toString();
  }

}
