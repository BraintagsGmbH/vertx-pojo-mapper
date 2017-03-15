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
package de.braintags.vertx.jomnigate.dataaccess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ArrayTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.BooleanTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.CalendarTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.CollectionTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.DateTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EmbeddedArrayTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EmbeddedListTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EmbeddedMapTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EmbeddedSingleTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EmbeddedSingleTest_Null;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.EnumTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.LocaleTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.MapTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.MiscTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.NumericTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ObjectNodeTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.PriceTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.PropertiesTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ReferencedArrayTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ReferencedListTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ReferencedMapTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.ReferencedSingleTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.StringTest;
import de.braintags.vertx.jomnigate.dataaccess.datatypetests.geo.GeoPointTest;

/**
 * Testing different datatypes for insert, update and query
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ StringTest.class, ArrayTest.class, BooleanTest.class, CalendarTest.class, CollectionTest.class,
    DateTest.class, EnumTest.class, LocaleTest.class, MapTest.class, MiscTest.class, NumericTest.class, PriceTest.class,
    PropertiesTest.class, GeoPointTest.class, ObjectNodeTest.class, EmbeddedSingleTest.class,
    EmbeddedSingleTest_Null.class, EmbeddedArrayTest.class, EmbeddedListTest.class, EmbeddedMapTest.class,
    ReferencedSingleTest.class, ReferencedArrayTest.class, ReferencedListTest.class, ReferencedMapTest.class })
public class DataTypesTestSuite {

}
