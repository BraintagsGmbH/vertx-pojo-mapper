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
package de.braintags.vertx.jomnigate.testdatastore.typehandler.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;

/**
 * A suite which is testing {@link ITypeHandler} and {@link IColumnHandler} for various datatypes
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ReferencedSingleTest.class, ReferencedArrayTest.class, ReferencedListTest.class,
    ReferencedMapTest.class })
public class JsonTypeHandlerTestSuiteReferenced {

}
