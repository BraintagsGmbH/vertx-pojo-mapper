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
package de.braintags.io.vertx.pojomapper.mongo.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.test.TestBaseTest;
import de.braintags.io.vertx.pojomapper.test.TestLifecycle;
import de.braintags.io.vertx.pojomapper.test.TestMongoMapper;
import de.braintags.io.vertx.pojomapper.test.TestOnlyIdMapper;
import de.braintags.io.vertx.pojomapper.test.TestSimpleInsert;
import de.braintags.io.vertx.pojomapper.test.TestSimpleMapper;
import de.braintags.io.vertx.pojomapper.test.TypeHandlerTest;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ TestBaseTest.class, TestMongoMapper.class, TestSimpleInsert.class, TestSimpleMapper.class,
    TestOnlyIdMapper.class, TypeHandlerTest.class, TestLifecycle.class })
public class AllTestsPojongo {

}
