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
package de.braintags.vertx.jomnigate.dataaccess.datatypetests;

import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.ResultContainer;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedMapper_Array;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.SimpleMapperEmbedded;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for testing embedded Arrays
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedArrayTest extends AbstractDatatypeTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(EmbeddedArrayTest.class);

  public EmbeddedArrayTest() {
    super("simpleMapper");
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, EmbeddedMapper_Array.class.getSimpleName());
    EmbeddedMapper_Array record = new EmbeddedMapper_Array();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<EmbeddedMapper_Array> query = getDataStore(context).createQuery(EmbeddedMapper_Array.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    EmbeddedMapper_Array loaded = (EmbeddedMapper_Array) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new EmbeddedMapper_Array();
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.datatypetests.AbstractDatatypeTest#validateAfterSave(io.vertx.ext.unit.
   * TestContext, java.lang.Object, de.braintags.vertx.jomnigate.testdatastore.ResultContainer)
   */
  @Override
  protected void validateAfterSave(TestContext context, Object record, ResultContainer resultContainer) {
    super.validateAfterSave(context, record, resultContainer);
    EmbeddedMapper_Array loaded = (EmbeddedMapper_Array) record;
    context.assertNotNull(loaded.simpleMapper[0].id);
    updateRecord(context, loaded);
  }

  private void updateRecord(TestContext context, EmbeddedMapper_Array loaded) {
    SimpleMapperEmbedded[] content = new SimpleMapperEmbedded[loaded.simpleMapper.length + 1];
    System.arraycopy(loaded.simpleMapper, 0, content, 0, loaded.simpleMapper.length);
    loaded.simpleMapper = content;
    loaded.simpleMapper[loaded.simpleMapper.length - 1] = new SimpleMapperEmbedded("the last stand", "jojo");
    LOGGER.info("Updating record now");
    saveRecord(context, loaded);
  }

}
