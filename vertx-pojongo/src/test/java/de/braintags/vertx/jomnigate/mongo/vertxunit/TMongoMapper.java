/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.vertxunit;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.EncoderSettings;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mongo.init.MongoDataStoreInit;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.TestHelper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.MapRecord;
import de.braintags.vertx.util.security.crypt.impl.StandardEncoder;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoMapper extends DatastoreBaseTest {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoMapper.class);

  @Test
  public void testSerializeDeser(TestContext context) {
    try {
      MapRecord record = new MapRecord();
      ObjectMapper mapper = ((JsonDatastore) getDataStore(context)).getJacksonMapper();
      String serialized = mapper.writeValueAsString(record);
      MapRecord rec = mapper.readValue(serialized, MapRecord.class);
    } catch (Exception e) {
      LOGGER.error("", e);
      context.fail(e);
    }
  }

  @Test
  public void storeDatastoreSettings(TestContext context) {
    DataStoreSettings ds = MongoDataStoreInit.createDefaultSettings();
    String serialized = Json.encodePrettily(ds);
    LOGGER.info(serialized);
    DataStoreSettings ds2 = Json.decodeValue(serialized, DataStoreSettings.class);
    context.assertNotNull(ds2);
    context.assertNotNull(ds2.getEncoders());
    context.assertEquals(1, ds2.getEncoders().size());
    EncoderSettings enc1 = ds.getEncoders().get(0);
    EncoderSettings enc2 = ds2.getEncoders().get(0);
    context.assertEquals(enc1.getEncoderClass(), enc2.getEncoderClass());
    context.assertEquals(enc1.getName(), enc2.getName());
    context.assertEquals(enc1.getProperties().get(StandardEncoder.SALT_PROPERTY),
        enc2.getProperties().get(StandardEncoder.SALT_PROPERTY));
  }

  @Test
  public void simpleTest(TestContext context) {
    LOGGER.info("-->>test");
    context.assertNotNull(TestHelper.getDatastoreContainer(context));
  }

  @Test
  public void testId(TestContext context) {
    LOGGER.info("-->> testId");
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
    IProperty idField = mapper.getField("id");
    context.assertNotNull(idField); // "Improve that the name of the id field is 'id'",

    Id ann = (Id) idField.getAnnotation(Id.class);
    if (ann == null)
      context.fail("Annotation Id must not be null");

    IProperty field = mapper.getIdInfo().getField();
    context.assertNotNull(field);
    context.assertTrue(field == idField);

    String javaName = field.getName();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    context.assertNotNull(ci);
    context.assertEquals("_id", ci.getName());
    String dbName = ci.getName();

    context.assertNotEquals(javaName, dbName);

    try {
      field = mapper.getField("doesntexist");
      context.fail("this should throw an exception here");
    } catch (NoSuchFieldException e) {
      // this is the expected result
    }

  }

  @Test
  public void testTransient(TestContext context) {
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);

    try {
      IProperty trField = mapper.getField("transientString");
      context.fail("transient fields should be mapped");
    } catch (NoSuchFieldException e) {
      // this is the expected result
    }

  }

  @Test
  public void testMetaData(TestContext context) {
    LOGGER.info("-->> testMetaData");
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
    ITableInfo ti = mapper.getTableInfo();
    context.assertNotNull(ti);
    context.assertEquals("MiniMapper", ti.getName());
    List<String> colNames = ti.getColumnNames();
    context.assertEquals(2, colNames.size());
  }

}
