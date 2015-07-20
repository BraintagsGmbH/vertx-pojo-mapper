/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.Index;
import de.braintags.io.vertx.pojomapper.annotation.IndexOptions;
import de.braintags.io.vertx.pojomapper.annotation.Indexes;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.impl.DummyDataStore;
import de.braintags.io.vertx.pojomapper.impl.DummyObjectFactory;
import de.braintags.io.vertx.pojomapper.mapper.Animal;
import de.braintags.io.vertx.pojomapper.mapper.Person;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.ObjectTypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestMapperFactory {
  public static final int NUMBER_OF_PROPERTIES = Person.NUMBER_OF_PROPERTIES;
  private static IDataStore dataStore = new DummyDataStore();
  private static IMapper mapperDef = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mapperDef = dataStore.getMapperFactory().getMapper(Person.class);
  }

  @Test
  public void testNumberOfProperties() {
    Assert.assertEquals("unexpected numer of properties", NUMBER_OF_PROPERTIES, mapperDef.getFieldNames().size());
  }

  @Test
  public void testNumberOfBeforeLoadMethods() {
    List<Method> beforeLoadMethods = mapperDef.getLifecycleMethods(BeforeLoad.class);
    Assert.assertEquals("unexpected number of BeforeLoad-Methods", 2, beforeLoadMethods.size());
  }

  @Test
  public void testObjectFactory() {
    IObjectFactory of = mapperDef.getObjectFactory();
    if (of == null)
      Assert.fail("ObjectFactory must not be null");
    else
      Assert.assertEquals("wrong ObjectFactory", DummyObjectFactory.class, of.getClass());
  }

  @Test
  public void testEntity() {
    Entity entity = mapperDef.getEntity();
    if (entity == null)
      Assert.fail("Entity must not be null");
    else
      Assert.assertEquals("wrong name in Entity", "PersonColumn", entity.name());
  }

  @Test
  public void testIndex() {
    Indexes ann = (Indexes) mapperDef.getAnnotation(Indexes.class);
    if (ann == null)
      Assert.fail("Annotation for Indexes must not be null");
    else {
      Assert.assertEquals("wrong number of indexes", 1, ann.value().length);
      Index index = ann.value()[0];
      Assert.assertEquals("The name of the index is wrong", "testIndex", index.name());
      Assert.assertEquals("wrong number of fields", 2, index.fields().length);

      IndexOptions options = index.options();
      if (options == null)
        Assert.fail("IndexOptions must not be null");
      else {
        Assert.assertEquals("wrong parameter unique in IndexOptions", false, options.unique());
      }
    }
  }

  @Test
  public void testProperty() {
    assertTrue(mapperDef.getField("weight").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("animal").getPropertyMapper() instanceof IReferencedMapper);
    assertTrue(mapperDef.getField("chicken").getPropertyMapper() instanceof IEmbeddedMapper);
    assertTrue(mapperDef.getField("intValue").getPropertyMapper() instanceof IPropertyMapper);

  }

  @Test
  public void testTypeHandler() {
    ITypeHandler th = mapperDef.getField("name").getTypeHandler();
    assertTrue("Not an instance of CharacterTypeHandler", th instanceof ObjectTypeHandler);
    th = mapperDef.getField("rabbit").getTypeHandler();
    assertTrue("Not an instance of ObjectTypeHandler", th instanceof ObjectTypeHandler);

    th = mapperDef.getField("chicken").getTypeHandler();
    assertNull("@Embedded must be null", th);

    assertNull("@Referenced must be null", mapperDef.getField("animal").getTypeHandler());

  }

  @Test
  public void testPropertyMapper() {
    Property ann = (Property) mapperDef.getField("weight").getAnnotation(Property.class);
    if (ann == null)
      Assert.fail("Annotation Property must not be null");
    else
      assertEquals("wrong name in Property", "WEIGHT", ann.value());
  }

  @Test
  public void testConstructor() {
    IField mapperField = mapperDef.getField("timeStamp");
    Constructor<?> con = mapperField.getConstructor();
    con = mapperField.getConstructor();
    Assert.assertNull(con);
    con = mapperField.getConstructor(long.class);
    Assert.assertNotNull(con);
    con = mapperField.getConstructor(Long.class);
    Assert.assertNull(con);

    mapperField = mapperDef.getField("name");
    con = mapperField.getConstructor();
    Assert.assertNotNull(con);

    mapperField = mapperDef.getField("listWithConstructor");
    con = mapperField.getConstructor();
    Assert.assertNotNull(con);

  }

  @Test
  public void testId() {
    Id ann = (Id) mapperDef.getField("idField").getAnnotation(Id.class);
    if (ann == null)
      Assert.fail("Annotation Id must not be null");
  }

  @Test
  public void testReferenced() {
    IField field = mapperDef.getField("animal");
    Referenced ann = (Referenced) field.getAnnotation(Referenced.class);
    if (ann == null)
      Assert.fail("Annotation Referenced must not be null");
  }

  @Test
  public void testGetAnnotatedFields() {
    IField[] fields = mapperDef.getAnnotatedFields(Referenced.class);
    if (fields == null || fields.length != 1)
      Assert.fail("WrongNumber of annotated fields with Referenced");
  }

  @Test
  public void testParametrizedField() {
    IField field = mapperDef.getField("stories");
    Assert.assertFalse("this should not be a single value", field.isSingleValue());
    Assert.assertFalse("this should not be an array", field.isArray());
    Assert.assertTrue("this should be a Collection", field.isCollection());
    Assert.assertEquals(1, field.getTypeParameters().size());
  }

  @Test
  public void testSubType() {
    IField field = mapperDef.getField("stories");
    Assert.assertEquals("subtype should be String", String.class, field.getSubType());
    Assert.assertEquals("subclass should be String", String.class, field.getSubClass());

    field = mapperDef.getField("name");
    Assert.assertNull(field.getSubType());
    Assert.assertNull(field.getSubClass());

  }

  @Test
  public void testWildcard() {
    IField field = mapperDef.getField("myClass");
    Assert.assertEquals(1, field.getTypeParameters().size());
  }

  @Test
  public void testMap() {
    IField field = mapperDef.getField("myMap");
    Assert.assertTrue(field.isMap());
    Assert.assertEquals(2, field.getTypeParameters().size());
  }

  @Test
  public void testList() {
    IField field = mapperDef.getField("listAnimals");
    Assert.assertTrue(field.isCollection());
    Class subClass = field.getSubClass();
    Type subType = field.getSubType();

    Assert.assertEquals(subClass, Animal.class);
    Assert.assertEquals(subType, Animal.class);
  }

  @Test
  public void testUnknownSubtype() {
    IField field = mapperDef.getField("unknownSubType");
    Assert.assertTrue(field.isCollection());
    Class subClass = field.getSubClass();
    Type subType = field.getSubType();

    Assert.assertEquals(subClass, Object.class);
    Assert.assertEquals(subType, Object.class);
  }

}
