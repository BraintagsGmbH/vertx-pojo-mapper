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
package de.braintags.io.vertx.pojomapper.datastoretest.typehandler;

import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.ArrayRecord;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ArrayTest extends AbstractTypeHandlerTest {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    return new ArrayRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "array";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler";
  }

}
