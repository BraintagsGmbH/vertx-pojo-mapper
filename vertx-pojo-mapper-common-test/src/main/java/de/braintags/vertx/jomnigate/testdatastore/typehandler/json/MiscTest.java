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
package de.braintags.vertx.jomnigate.testdatastore.typehandler.json;

import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.MiscMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MiscTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, MiscMapper.class.getSimpleName());
    MiscMapper record = new MiscMapper();
    record.myCharacter = null;
    saveRecord(context, record);
    IQuery<MiscMapper> query = getDataStore(context).createQuery(MiscMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    MiscMapper loaded = (MiscMapper) list.get(0);
    context.assertNull(loaded.myCharacter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    MiscMapper mapper = new MiscMapper();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "myCharacter";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.vertx.jomnigate.json.typehandler.handler.CharacterTypeHandler";
  }

}
