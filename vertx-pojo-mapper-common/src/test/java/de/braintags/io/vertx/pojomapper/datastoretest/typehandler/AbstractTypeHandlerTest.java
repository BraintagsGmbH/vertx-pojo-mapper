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

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.ResultContainer;
import de.braintags.io.vertx.pojomapper.exception.MappingException;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractTypeHandlerTest extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractTypeHandlerTest.class);

  @Test
  public void testSaveAndReadRecord() {
    BaseRecord record = createInstance();
    dropTable(record.getClass().getSimpleName());
    ResultContainer resultContainer = saveRecord(record);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<? extends BaseRecord> query = getDataStore().createQuery(record.getClass());
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        assertTrue(record.equals(result.result()));
        LOGGER.info("finished!");
      }
    });

  }

  /**
   * Create the instance of
   * 
   * @return
   */
  public abstract BaseRecord createInstance();

  class BaseRecord {
    @Id
    public long id;

    @Override
    public boolean equals(Object ob) {
      Field[] fields = getClass().getFields();
      for (Field field : fields) {
        compare(field, ob);
      }

      return true;
    }

    private boolean compare(Field field, Object compare) {
      if (field.getName().equals("id"))
        return true;
      if (field.getName().equals("buffer")) {
        @SuppressWarnings("unused")
        String test = "test ";
      }
      try {
        Object value = field.get(this);
        Object compareValue = field.get(compare);
        equalValues(value, compareValue, field.getName());
        return true;
      } catch (Exception e) {
        throw new RuntimeException("Error in field " + field.getName(), e);
      }

    }

    private boolean equalValues(Object value, Object compareValue, String fieldName) {
      if (value == null && compareValue == null)
        return true;
      if (value instanceof CharSequence) {
        value = value.toString();
        compareValue = compareValue.toString();
      }

      if (value.getClass().isArray()) {
        if (!compareValue.getClass().isArray())
          throw new MappingException("Contents are not equal: " + fieldName);
        for (int i = 0; i < Array.getLength(value); i++) {
          if (!Array.get(value, i).equals(Array.get(compareValue, i)))
            throw new MappingException("Contents are not equal: " + fieldName);
        }
        return true;
      }

      if (!value.equals(compareValue))
        throw new MappingException("Contents are not equal: " + fieldName);
      return true;
    }

  }
}
