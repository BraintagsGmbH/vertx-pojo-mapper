/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexOptions;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.impl.DummyDataStore;
import de.braintags.vertx.jomnigate.impl.DummyObjectFactory;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.IntegerTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.StringTypeHandler;
import de.braintags.vertx.jomnigate.mapper.Animal;
import de.braintags.vertx.jomnigate.mapper.Person;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.impl.ParametrizedMappedField;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TMapperFactory {
  public static final int NUMBER_OF_PROPERTIES = Person.NUMBER_OF_PROPERTIES;
  public static IDataStore dataStore = new DummyDataStore();
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
  public void testIgnore() {
    try {
      mapperDef.getField("ignoreField");
      Assert.fail("expected NoSuchFieldException");
    } catch (NoSuchFieldException e) {
      // expected result
    }
    try {
      mapperDef.getField("ignoreField2");
      Assert.fail("expected NoSuchFieldException");
    } catch (NoSuchFieldException e) {
      // expected result
    }

  }

  @Test
  public void testNumberOfBeforeLoadMethods() {
    List<IMethodProxy> beforeLoadMethods = mapperDef.getLifecycleMethods(BeforeLoad.class);
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
    assertTrue(mapperDef.getField("animal").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("chicken").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("intValue").getPropertyMapper() instanceof IPropertyMapper);

  }

  @Test
  public void testTypeHandler() {
    checkTypeHandler(mapperDef, "name", StringTypeHandler.class, null);
    checkTypeHandler(mapperDef, "rabbit", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "chicken", ObjectTypeHandlerEmbedded.class, null);
    checkTypeHandler(mapperDef, "animal", ObjectTypeHandlerReferenced.class, null);

    IField field = mapperDef.getField("myMap"); // public Map<Integer, Double> myMap;
    assertTrue("wrong TypeHandler: " + field.getTypeHandler(), field.getTypeHandler() instanceof MapTypeHandler);
    MapTypeHandler mth = (MapTypeHandler) field.getTypeHandler();
    ITypeHandler keyTh = mth.getKeyTypeHandler(new Integer(5), field);
    assertTrue(keyTh instanceof IntegerTypeHandler);

    checkTypeHandler(mapperDef, "listAnimals", CollectionTypeHandler.class, ObjectTypeHandler.class);
    checkTypeHandler(mapperDef, "chickenFarm", CollectionTypeHandlerEmbedded.class, ObjectTypeHandlerEmbedded.class);
    checkTypeHandler(mapperDef, "dogFarm", CollectionTypeHandlerReferenced.class, ObjectTypeHandlerReferenced.class);

    checkTypeHandler(mapperDef, "myMap", MapTypeHandler.class, ObjectTypeHandler.class);
    checkTypeHandler(mapperDef, "myMapEmbedded", MapTypeHandlerEmbedded.class, ObjectTypeHandlerEmbedded.class);
    checkTypeHandler(mapperDef, "myMapReferenced", MapTypeHandlerReferenced.class, ObjectTypeHandlerReferenced.class);

    checkTypeHandler(mapperDef, "animalArray", ArrayTypeHandler.class, ObjectTypeHandler.class);
    checkTypeHandler(mapperDef, "animalArrayEmbedded", ArrayTypeHandlerEmbedded.class, ObjectTypeHandlerEmbedded.class);
    checkTypeHandler(mapperDef, "animalArrayReferenced", ArrayTypeHandlerReferenced.class,
        ObjectTypeHandlerReferenced.class);

  }

  private void checkTypeHandler(IMapper mapperdef, String fieldName, Class expectedTh, Class expectedSubTypeHandler) {
    IField field = mapperDef.getField(fieldName);
    assertNotNull("Typehandler must not be null for field: " + field.getFullName(), field.getTypeHandler());
    assertEquals("wrong TypeHandler for field: " + field.getFullName(), expectedTh, field.getTypeHandler().getClass());
    if (expectedSubTypeHandler != null)
      assertEquals("wrong SubTypeHandler for field: " + field.getFullName(), expectedSubTypeHandler,
          field.getSubTypeHandler().getClass());
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
    assertNull(con);

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

    IField field = mapperDef.getIdField();
    assertNotNull(field);
    IField field2 = mapperDef.getField(field.getName());
    Assert.assertSame(field, field2);

  }

  @Test
  public void testReferenced() {
    IField field = mapperDef.getField("animal");
    Referenced ann = (Referenced) field.getAnnotation(Referenced.class);
    if (ann == null)
      Assert.fail("Annotation Referenced must not be null");
  }

  @Test
  public void testArray() {
    IField field = mapperDef.getField("stringArray");
    assertTrue(field.isArray());
    assertEquals(ArrayTypeHandler.class, field.getTypeHandler().getClass());
  }

  @Test
  public void testGetAnnotatedFields() {
    IField[] fields = mapperDef.getAnnotatedFields(Referenced.class);
    if (fields == null || fields.length == 0)
      fail("WrongNumber of annotated fields with Referenced");
  }

  @Test
  public void testParametrizedField() {
    IField field = mapperDef.getField("stories");
    assertFalse("this should not be a single value", field.isSingleValue());
    Assert.assertFalse("this should not be an array", field.isArray());
    assertTrue("this should be a Collection", field.isCollection());
    assertEquals(1, field.getTypeParameters().size());

    field = mapperDef.getField("myMap");
    boolean parametrizedField = false;
    for (IField parField : field.getTypeParameters()) {
      if (parField instanceof ParametrizedMappedField)
        parametrizedField = true;
    }
    Assert.assertTrue("tpyeParameters of field 'myMap' must contain ParametrizedMappedField", parametrizedField);

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

    assertEquals(Integer.class, field.getMapKeyClass());
    assertEquals(Double.class, field.getSubClass());
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
