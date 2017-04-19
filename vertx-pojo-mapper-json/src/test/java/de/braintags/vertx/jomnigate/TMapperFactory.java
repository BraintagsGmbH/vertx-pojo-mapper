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
import de.braintags.vertx.jomnigate.annotation.IndexOption;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.impl.DummyDataStore;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.mapper.Animal;
import de.braintags.vertx.jomnigate.mapper.NoReferencedFieldMapper;
import de.braintags.vertx.jomnigate.mapper.Person;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.IObjectFactory;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.IndexOption.IndexFeature;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mapping.impl.ParametrizedMappedField;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TMapperFactory {
  public static final int NUMBER_OF_PROPERTIES = Person.NUMBER_OF_PROPERTIES;
  public static IDataStore dataStore = new DummyDataStore();
  private static IMapper<Person> mapperDef = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mapperDef = dataStore.getMapperFactory().getMapper(Person.class);
  }

  @Test
  public void testReferencedField() {
    Assert.assertTrue("this mapper has referenced fields", ((Mapper) mapperDef).hasReferencedFields());
    IMapper<NoReferencedFieldMapper> mapper = dataStore.getMapperFactory().getMapper(NoReferencedFieldMapper.class);
    Assert.assertFalse("this mapper has NO referenced fields", ((Mapper) mapper).hasReferencedFields());
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
    if (!mapperDef.getClass().getName().contains("Mongo")) {
      IObjectFactory of = mapperDef.getObjectFactory();
      Assert.assertNotNull("ObjectFactory must not be null", of);
    }
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
    Indexes ann = mapperDef.getAnnotation(Indexes.class);
    if (ann == null)
      Assert.fail("Annotation for Indexes must not be null");
    else {
      Assert.assertEquals("wrong number of indexes", 1, ann.value().length);
      Index index = ann.value()[0];
      Assert.assertEquals("The name of the index is wrong", "testIndex", index.name());
      Assert.assertEquals("wrong number of fields", 2, index.fields().length);

      IndexOption[] options = index.options();
      if (options == null)
        Assert.fail("IndexOptions must not be null");
      else {
        Assert.assertEquals(1, options.length);
        Assert.assertEquals(IndexFeature.UNIQUE, options[0].feature());
        Assert.assertEquals("wrong parameter unique in IndexOptions", false, options[0].value());
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
    checkTypeHandler(mapperDef, "name", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "rabbit", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "chicken", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "animal", ObjectTypeHandlerReferenced.class, null);

    checkTypeHandler(mapperDef, "listAnimals", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "chickenFarm", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "dogFarm", CollectionTypeHandlerReferenced.class, ObjectTypeHandlerReferenced.class);

    checkTypeHandler(mapperDef, "myMap", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "myMapEmbedded", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "myMapReferenced", MapTypeHandlerReferenced.class, ObjectTypeHandlerReferenced.class);

    checkTypeHandler(mapperDef, "animalArray", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "animalArrayEmbedded", ObjectTypeHandler.class, null);
    checkTypeHandler(mapperDef, "animalArrayReferenced", ArrayTypeHandlerReferenced.class,
        ObjectTypeHandlerReferenced.class);

  }

  private void checkTypeHandler(final IMapper mapperdef, final String fieldName, final Class expectedTh, final Class expectedSubTypeHandler) {
    IProperty field = mapperDef.getField(fieldName);
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
    IProperty mapperField = mapperDef.getField("timeStamp");
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

    IProperty field = mapperDef.getIdInfo().getField();
    assertNotNull(field);
    IProperty field2 = mapperDef.getField(field.getName());
    Assert.assertSame(field, field2);

  }

  @Test
  public void testReferenced() {
    IProperty field = mapperDef.getField("animal");
    Referenced ann = (Referenced) field.getAnnotation(Referenced.class);
    Assert.assertNotNull("Annotation Referenced must not be null", ann);
  }

  @Test
  public void testArray() {
    IProperty field = mapperDef.getField("stringArray");
    assertTrue(field.isArray());
    assertEquals(ObjectTypeHandler.class, field.getTypeHandler().getClass());
  }

  @Test
  public void testGetAnnotatedFields() {
    IProperty[] fields = mapperDef.getAnnotatedFields(Referenced.class);
    if (fields == null || fields.length == 0)
      fail("WrongNumber of annotated fields with Referenced");
  }

  @Test
  public void testParametrizedField() {
    IProperty field = mapperDef.getField("stories");
    assertFalse("this should not be a single value", field.isSingleValue());
    Assert.assertFalse("this should not be an array", field.isArray());
    assertTrue("this should be a Collection", field.isCollection());
    assertEquals(1, field.getTypeParameters().size());

    field = mapperDef.getField("myMap");
    boolean parametrizedField = false;
    for (IProperty parField : field.getTypeParameters()) {
      if (parField instanceof ParametrizedMappedField)
        parametrizedField = true;
    }
    Assert.assertTrue("tpyeParameters of field 'myMap' must contain ParametrizedMappedField", parametrizedField);

  }

  @Test
  public void testSubType() {
    IProperty field = mapperDef.getField("stories");
    Assert.assertEquals("subtype should be String", String.class, field.getSubType());
    Assert.assertEquals("subclass should be String", String.class, field.getSubClass());

    field = mapperDef.getField("name");
    Assert.assertNull(field.getSubType());
    Assert.assertNull(field.getSubClass());

  }

  @Test
  public void testWildcard() {
    IProperty field = mapperDef.getField("myClass");
    Assert.assertEquals(1, field.getTypeParameters().size());
  }

  @Test
  public void testMap() {
    IProperty field = mapperDef.getField("myMap");

    Assert.assertTrue(field.isMap());
    Assert.assertEquals(2, field.getTypeParameters().size());

    assertEquals(Integer.class, field.getMapKeyClass());
    assertEquals(Double.class, field.getSubClass());
  }

  @Test
  public void testList() {
    IProperty field = mapperDef.getField("listAnimals");
    Assert.assertTrue(field.isCollection());
    Class subClass = field.getSubClass();
    Type subType = field.getSubType();

    Assert.assertEquals(subClass, Animal.class);
    Assert.assertEquals(subType, Animal.class);
  }

  @Test
  public void testUnknownSubtype() {
    IProperty field = mapperDef.getField("unknownSubType");
    Assert.assertTrue(field.isCollection());
    Class subClass = field.getSubClass();
    Type subType = field.getSubType();

    Assert.assertEquals(subClass, Object.class);
    Assert.assertEquals(subType, Object.class);
  }

}
