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
package de.braintags.io.vertx.pojomapper.datastoretest.typehandler;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.ArrayRecord;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ArrayTest extends AbstractTypeHandlerTest {

  @Test
  public void testTypeHandler() {
    BaseRecord record = createInstance();
    IMapper mapper = getDataStore().getMapperFactory().getMapper(record.getClass());
    IField field = mapper.getField("array");
    ITypeHandler th = field.getTypeHandler();
    assertNotNull(th);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    return new ArrayRecord();
  }

}
