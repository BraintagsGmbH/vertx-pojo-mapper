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
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.JsonMapper;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class JsonTest extends AbstractTypeHandlerTest {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    JsonMapper mapper = new JsonMapper();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "json";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler";
  }

}
