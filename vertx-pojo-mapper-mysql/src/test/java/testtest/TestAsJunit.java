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
package testtest;

import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.ReferenceMapper_Array;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestAsJunit extends AbstractTypeHandlerTest {

  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mongo.testNew.MongoDataStoreContainer
  // -Djava.util.logging.config.file=src/main/resources/logging.properties -DBlockedThreadCheckInterval=10000
  // -DWarningExceptionTime=10000 -DstartMongoLocal=false

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    BaseRecord mapper = new ReferenceMapper_Array(50);
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "simpleMapper";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerReferenced";
  }

}
