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
package de.braintags.io.vertx.pojomapper.datastoretest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.TypeHandlerTestSuite;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.TypeHandlerTestSuiteEmbedded;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.TypeHandlerTestSuiteReferenced;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestBaseTest.class, TestSimpleInsert.class, TestSimpleMapper.class, TestOnlyIdMapper.class,
    TypeHandlerTestSuite.class, TypeHandlerTestSuiteEmbedded.class, TypeHandlerTestSuiteReferenced.class })
public class AllTestsCommon {

}
