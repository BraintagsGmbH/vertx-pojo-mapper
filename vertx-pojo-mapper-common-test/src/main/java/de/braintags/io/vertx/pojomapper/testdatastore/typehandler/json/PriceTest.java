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
import de.braintags.io.vertx.pojomapper.datatypes.Price;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.PriceMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class PriceTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, PriceMapper.class.getSimpleName());
    PriceMapper record = new PriceMapper();
    record.price = null;
    saveRecord(context, record);
    IQuery<PriceMapper> query = getDataStore(context).createQuery(PriceMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    PriceMapper loaded = (PriceMapper) list.get(0);
    context.assertNull(loaded.price);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    PriceMapper mapper = new PriceMapper();
    mapper.price = new Price("20.88");
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "price";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.PriceTypeHandler";
  }

}
