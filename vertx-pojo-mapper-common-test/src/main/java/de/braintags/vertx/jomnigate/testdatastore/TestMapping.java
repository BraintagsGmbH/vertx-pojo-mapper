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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexOptions;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMethodProxy;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.Animal;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper_BeanMethodWithoutField;
import de.braintags.vertx.jomnigate.testdatastore.mapper.Person;
import de.braintags.vertx.jomnigate.testdatastore.mapper.PolyMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.PrivateIdMapper;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestMapping extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestMapping.class);
  public static final int NUMBER_OF_PROPERTIES = Person.NUMBER_OF_PROPERTIES;

  private static IMapper mapperDef = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUpBeforeClass(final TestContext context) throws Exception {
    mapperDef = getDataStore(context).getMapperFactory().getMapper(Person.class);
  }

  @Test
  public void testClassAnnotation(final TestContext context) {
    JsonTypeInfo ti = PolyMapper.class.getAnnotation(JsonTypeInfo.class);
    JsonTypeInfo[] til = PolyMapper.class.getAnnotationsByType(JsonTypeInfo.class);
    context.assertNotNull(til);
  }

  @Test
  public void testPrivateIdField(final TestContext context) {
    IMapper<PrivateIdMapper> mapper = getDataStore(context).getMapperFactory().getMapper(PrivateIdMapper.class);
  }

  @Test
  public void simpleTest(final TestContext context) {
    IMapper<MiniMapper> mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
  }

  @Test
  public void testMiniMapper_BeanMethodWithoutField(final TestContext context) {
    IMapper<MiniMapper_BeanMethodWithoutField> mapper = getDataStore(context).getMapperFactory()
        .getMapper(MiniMapper_BeanMethodWithoutField.class);
  }

  @Test
  public void testPropertyMapper() {
    String classNamePart = "JacksonPropertyMapper";
    checkPropertyhandler(mapperDef, "name", classNamePart);
    checkPropertyhandler(mapperDef, "listAnimals", classNamePart);
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
        Assert.assertFalse("wrong parameter unique in IndexOptions", options.unique());
      }
    }
  }

  @Test
  public void testPropertyAccessor() {
    assertNotNull("undefined PropertyAccessor", mapperDef.getField("weight").getPropertyAccessor());
  }

  @Test
  public void testProperty() {
    assertTrue(mapperDef.getField("weight").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("animal").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("chicken").getPropertyMapper() instanceof IPropertyMapper);
    assertTrue(mapperDef.getField("intValue").getPropertyMapper() instanceof IPropertyMapper);

  }

  @Test
  public void testJsonMapping(final TestContext context) {
    try {
      ObjectMapper mapper = Json.mapper;
      examine(mapper, MiniMapper.class);
      examine(mapper, MiniMapper_BeanMethodWithoutField.class);
      examine(mapper, Person.class);
    } catch (Throwable e) {
      LOGGER.error("", e);
      context.fail(e);
    }

  }

  @Test
  public void testPropertyAnnotation() {
    Property ann = (Property) mapperDef.getField("weight").getAnnotation(Property.class);
    if (ann == null)
      Assert.fail("Annotation Property must not be null");
    else
      assertEquals("wrong name in Property", "WEIGHT", ann.value());
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
    if (ann == null)
      Assert.fail("Annotation Referenced must not be null");
  }

  @Test
  public void testArray() {
    IProperty field = mapperDef.getField("stringArray");
    assertTrue(field.isArray());
  }

  @Test
  public void testGetAnnotatedFields() {
    IProperty[] fields = mapperDef.getAnnotatedFields(Referenced.class);
    if (fields == null || fields.length == 0)
      fail("WrongNumber of annotated fields with Referenced");
  }

  @Test
  public void testMap() {
    IProperty field = mapperDef.getField("myMap");
    Assert.assertTrue(field.isMap());
    assertEquals(Integer.class, field.getMapKeyClass());
    assertEquals(Double.class, field.getSubClass());
  }

  @Test
  public void testList() {
    IProperty field = mapperDef.getField("listAnimals");
    Assert.assertTrue(field.isCollection());
    Class subClass = field.getSubClass();
    Assert.assertEquals(subClass, Animal.class);
  }

  @Test
  public void testUnknownSubtype() {
    IProperty field = mapperDef.getField("unknownSubType");
    Assert.assertTrue(field.isCollection());
  }

  @Test
  public void testReferencedField() {
    Assert.assertTrue("this mapper has referenced fields", mapperDef.hasReferencedFields());
  }

  /**
   * @param mapper
   */
  private void examine(final ObjectMapper mapper, final Class mapperClass) {
    LOGGER.info("examine " + mapperClass.getName());
    JavaType type = mapper.constructType(mapperClass);
    BeanDescription desc = mapper.getSerializationConfig().introspect(type);

    for (BeanPropertyDefinition def : desc.findProperties()) {
      LOGGER.debug(def);
      LOGGER.debug(def.getFullName());
    }
  }

  private void checkPropertyhandler(final IMapper mapperdef, final String fieldName, final String classNamePart) {
    IProperty field = mapperDef.getField(fieldName);
    assertNotNull("property mapper must not be null for field: " + field.getFullName(), field.getPropertyMapper());
    String className = field.getPropertyMapper().getClass().getName();
    assertTrue("wrong property mapper for field: " + field.getFullName(), className.contains(classNamePart));

  }

  boolean useMapper(Class mapperDef) {
    return !mapperDef.getName().contains("Mongo") && !mapperDef.getName().contains("Mongo");

  }
}
