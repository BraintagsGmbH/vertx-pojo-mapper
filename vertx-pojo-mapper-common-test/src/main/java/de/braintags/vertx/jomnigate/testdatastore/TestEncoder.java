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

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.impl.AbstractDataStore;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperEncoded;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperEncodedWrongFieldClass;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperWrongEncodedName;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestEncoder extends DatastoreBaseTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestEncoder.class);

  @Test
  public void simpleTest(TestContext context) {
    String pw = "gghhgg";
    checkEncoderDefined(context);
    clearTable(context, MiniMapperEncoded.class.getSimpleName());
    MiniMapperEncoded sm = new MiniMapperEncoded();
    sm.password = pw;
    saveRecord(context, sm);
    context.assertNotEquals(pw, sm.password);
    LOGGER.info(sm.password);

    IQuery<MiniMapperEncoded> query = getDataStore(context).createQuery(MiniMapperEncoded.class);
    MiniMapperEncoded found = (MiniMapperEncoded) findFirst(context, query);
    context.assertNotEquals(pw, found.password);
    context.assertEquals(sm.password, found.password);

    LOGGER.info(found.password);

  }

  @Test
  public void testWrongFieldClass(TestContext context) {
    checkEncoderDefined(context);
    clearTable(context, MiniMapperEncodedWrongFieldClass.class.getSimpleName());
    try {
      IQuery<MiniMapperEncodedWrongFieldClass> query = getDataStore(context)
          .createQuery(MiniMapperEncodedWrongFieldClass.class);
      ResultContainer resultContainer = find(context, query, 0);
      context.fail("expected unsupportedOperationException here");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void testWrongEncoderName(TestContext context) {
    checkEncoderDefined(context);
    clearTable(context, MiniMapperWrongEncodedName.class.getSimpleName());
    try {
      IQuery<MiniMapperWrongEncodedName> query = getDataStore(context).createQuery(MiniMapperWrongEncodedName.class);
      ResultContainer resultContainer = find(context, query, 0);
      context.fail("expected unsupportedOperationException here");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  private void checkEncoderDefined(TestContext context) {
    context.assertFalse(((AbstractDataStore) getDataStore(context)).getEncoderMap().isEmpty(),
        "no encoders defined in datastore");
  }
}
