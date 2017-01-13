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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json;

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.LocaleRecord;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class LocaleTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, LocaleRecord.class.getSimpleName());
    LocaleRecord record = new LocaleRecord();
    record.locale = null;
    saveRecord(context, record);
    IQuery<LocaleRecord> query = getDataStore(context).createQuery(LocaleRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    LocaleRecord loaded = (LocaleRecord) list.get(0);
    context.assertNull(loaded.locale);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new LocaleRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "locale";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.LocaleTypeHandler";
  }

}
