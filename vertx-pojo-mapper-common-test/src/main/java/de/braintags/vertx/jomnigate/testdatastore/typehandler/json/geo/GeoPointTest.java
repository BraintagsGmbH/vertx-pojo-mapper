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
package de.braintags.vertx.jomnigate.testdatastore.typehandler.json.geo;

import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.geo.GeoPointRecord;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointTest extends AbstractTypeHandlerTest {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new GeoPointRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "point";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.vertx.jomnigate.mongo.typehandler.GeoPointTypeHandlerMongo";
  }

}
