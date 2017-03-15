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
import de.braintags.vertx.jomnigate.datatypes.Price;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.PriceMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class PriceTest extends AbstractDatatypeTest {

  public PriceTest() {
    super("price");
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, PriceMapper.class.getSimpleName());
    PriceMapper record = new PriceMapper();
    record.setPrice(null);
    saveRecord(context, record);
    IQuery<PriceMapper> query = getDataStore(context).createQuery(PriceMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    PriceMapper loaded = (PriceMapper) list.get(0);
    context.assertNull(loaded.getPrice());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    PriceMapper mapper = new PriceMapper();
    mapper.setPrice(new Price("20.88"));
    return mapper;
  }

}
