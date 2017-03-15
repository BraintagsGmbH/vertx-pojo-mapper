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
package de.braintags.vertx.jomnigate.testdatastore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.dataaccess.DataTypesTestSuite;
import de.braintags.vertx.jomnigate.dataaccess.query.TestFieldConditionCache;
import de.braintags.vertx.jomnigate.dataaccess.query.TestQueryInterator;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestBaseTest.class, TestSimpleMapper.class, TestStoreObject.class, TestQueryHelper.class,
    TestIndex.class, TestRoundtrip.class, TestSimpleMapperQuery.class, TestOnlyIdMapper.class, TestTrigger.class,
    TestMassInsert.class, TestKeyGenerator.class, TestGeoSearch.class, TestEncoder.class, TestEmbeddedEntity.class,
    TestListExtrems.class, TestReferenced.class, TestFieldConditionCache.class, TestQueryInterator.class,
    DataTypesTestSuite.class, TestClearDatastore.class })
public class AllTestsCommon {

}
