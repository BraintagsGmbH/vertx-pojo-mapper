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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * A suite which is testing {@link ITypeHandler} and {@link IColumnHandler} for various datatypes
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ EmbeddedListTest.class, EmbeddedMapTest.class, EmbeddedArrayTest.class, EmbeddedSingleTest_Null.class,
    EmbeddedSingleTest.class })
public class JsonTypeHandlerTestSuiteEmbedded {

}
