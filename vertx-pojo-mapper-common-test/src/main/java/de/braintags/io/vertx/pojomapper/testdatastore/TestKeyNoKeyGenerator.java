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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapperDebugGenerator;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Tests, where the default keygenerator is set to NULL
 * 
 * @author mremme
 * 
 */
public class TestKeyNoKeyGenerator extends DatastoreBaseTest {

  @Test
  public void testKeyGeneratorNoKeyGenerator(TestContext context) {
    // check that default keygenerator is defined
    IKeyGenerator keyGen = getDataStore(context).getDefaultKeyGenerator();
    context.assertNull(keyGen, "the default keygenerator must be null, but is " + keyGen);
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(SimpleMapper.class);
    context.assertNull(mapper.getKeyGenerator(), "the keygenerator must be null, but is " + mapper.getKeyGenerator());

    mapper = getDataStore(context).getMapperFactory().getMapper(KeyGeneratorMapper.class);
    context.assertTrue(mapper.getKeyGenerator() instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(mapper.getKeyGenerator()));

    clearTable(context, "SimpleMapper");
    SimpleMapper sm = new SimpleMapper();
    sm.name = "testName";
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",
    try {
      Integer.parseInt(sm.id);
    } catch (NumberFormatException e) {
      context.fail("Not a numeric ID: " + sm.id);
    }

  }

  /**
   * @param context
   * @throws AssertionError
   */
  private void testKeyGeneratorMongo(TestContext context) throws AssertionError {
    IKeyGenerator keyGen = getDataStore(context).getDefaultKeyGenerator();
    context.assertNotNull(keyGen, "keygenerator must not be null");
    context.assertTrue(keyGen instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(keyGen.getName()));

    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(KeyGeneratorMapper.class);
    context.assertTrue(mapper.getKeyGenerator() instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(mapper.getKeyGenerator()));
    clearTable(context, "KeyGeneratorMapper");
    KeyGeneratorMapper sm = new KeyGeneratorMapper();
    sm.name = "testName";
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",
    try {
      Integer.parseInt(sm.id);
    } catch (NumberFormatException e) {
      context.fail("Not a numeric ID: " + sm.id);
    }

    sm.name = "testNameModified";
    resultContainer = saveRecord(context, sm);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.UPDATE);
    context.assertEquals(String.valueOf(we1.getId()), String.valueOf(we.getId()),
        "id must not change after update: " + we1.getId() + " | " + we.getId());
  }

  /**
   * @param context
   * @return
   */
  private KeyGeneratorMapperDebugGenerator doInsert(TestContext context, String name) {
    KeyGeneratorMapperDebugGenerator km = new KeyGeneratorMapperDebugGenerator();
    km.name = name;
    ResultContainer resultContainer = saveRecord(context, km);
    checkWriteAction(context, resultContainer, WriteAction.INSERT);
    return km;
  }

  private void checkWriteAction(TestContext context, ResultContainer resultContainer, WriteAction we) {
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), we);
  }

  @BeforeClass
  public static void beforeClass() {
    AbstractDataStoreContainer.DEFAULT_KEY_GENERATOR = null;
  }
}
