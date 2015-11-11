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

import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.StringTestMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class StringTest extends AbstractTypeHandlerTest {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    StringTestMapper mapper = new StringTestMapper();
    mapper.stringField = "testcontent";
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "stringField";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.StringTypeHandler";
  }

}
