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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.TypeHandlerTestSuite;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestBaseTest.class, TestSimpleMapper.class, TestSimpleInsert.class, TestSimpleMapperQuery.class,
    TestOnlyIdMapper.class, TestTrigger.class, TypeHandlerTestSuite.class, TestKeyGenerator.class })
public class AllTestsCommon {

}
