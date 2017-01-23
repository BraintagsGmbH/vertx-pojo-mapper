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
package de.braintags.vertx.jomnigate.testdatastore.typehandler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.JsonTypeHandlerTestSuite;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.JsonTypeHandlerTestSuiteEmbedded;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.JsonTypeHandlerTestSuiteReferenced;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.geo.GeoTestSuite;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.stringbased.StringTypeHandlerTestSuite;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ StringTypeHandlerTestSuite.class, JsonTypeHandlerTestSuite.class,
    JsonTypeHandlerTestSuiteEmbedded.class, JsonTypeHandlerTestSuiteReferenced.class, GeoTestSuite.class })
public class TypeHandlerTestSuite {

}
