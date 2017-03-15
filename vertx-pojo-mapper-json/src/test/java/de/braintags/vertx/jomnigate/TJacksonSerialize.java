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

import org.junit.Assert;
import org.junit.Test;

import de.braintags.vertx.jomnigate.impl.DummyDataStore;
import de.braintags.vertx.jomnigate.mapper.Person;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EnumRecord;
import io.vertx.core.json.Json;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TJacksonSerialize extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TJacksonSerialize.class);

  public static final int NUMBER_OF_PROPERTIES = Person.NUMBER_OF_PROPERTIES;
  public static IDataStore dataStore = new DummyDataStore();

  @Test
  public void testEnum() {
    EnumRecord record = new EnumRecord();
    try {
      String value = Json.mapper.writeValueAsString(record);
      LOGGER.info(value);
      EnumRecord rec2 = Json.decodeValue(value, EnumRecord.class);
    } catch (Exception e) {
      LOGGER.error(e);
      Assert.fail(e.getMessage());
    }
  }

  // @Test
  // public void testExtendJacksonInject(TestContext context) {
  // List<String> valueList = new ArrayList<>();
  // InjectableValues iv = new InjectableValues.Std().addValue("demoKey", valueList);
  //
  // ObjectMapper mapper = Json.mapper;
  // mapper.registerModule(new JacksonModuleJomnigate(getDataStore(context)));
  // try {
  // ObjectWriter writer = mapper.writer();
  // writer.getConfig().getDefaultPropertyInclusion();
  // ContextAttributes ca = writer.getAttributes();
  // LOGGER.info("factory: " + writer.getFactory().getClass().getName());
  // JOmnigateFactory jf = new JOmnigateFactory(writer.getFactory(), mapper);
  // writer.with(jf);
  //
  // JsonMapper entity = new JsonMapper("testName");
  // String source = writer.writeValueAsString(entity);
  //
  // // String source = Json.encodePrettily(entity);
  //
  // LOGGER.info(source);
  // ObjectReader reader = mapper.reader(iv);
  // JsonMapper entity2 = reader.forType(JsonMapper.class).readValue(source);
  // context.assertFalse(valueList.isEmpty());
  // context.assertEquals(entity.id, entity2.id);
  //
  // // Now lets update an instance
  // JsonMapper updateEntity = new JsonMapper("NoName");
  // ObjectReader updateReader = mapper.reader(iv).withValueToUpdate(updateEntity);
  // updateReader.readValue(source);
  // context.assertEquals(entity.name, updateEntity.name);
  //
  // // context.assertEquals(entity.referencedSubmapper.id, entity2.referencedSubmapper.id);
  // // context.assertEquals(entity.referencedSubmapper.testString, entity2.referencedSubmapper.testString);
  //
  // } catch (Throwable e) {
  // LOGGER.error("", e);
  // Assert.fail(e.getMessage());
  // }
  //
  // }

}
