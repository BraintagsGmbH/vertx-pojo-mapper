/*-
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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import io.vertx.ext.unit.TestContext;

/**
 * Unit test for index generation of {@link IIndexedField}
 * 
 * @author sschmitt
 *
 */
public class TestIndexedFields extends DatastoreBaseTest {

  @SuppressWarnings("unchecked")
  @Test
  public void testIndexedField_noIndex(final TestContext context) {
    IMapper<TestMapperNoIndex> mapper = getDataStore(context).getMapperFactory().getMapper(TestMapperNoIndex.class);
    assertThat(mapper.getIndexDefinitions(), anyOf(nullValue(), empty()));
  }

  @Test
  public void testIndexedField_createIndex(final TestContext context) {
    IMapper<TestMapperIndex> mapper = getDataStore(context).getMapperFactory().getMapper(TestMapperIndex.class);
    assertThat(mapper.getIndexDefinitions(), hasSize(1));
    IIndexDefinition indexDefinition = mapper.getIndexDefinitions().iterator().next();
    assertThat(indexDefinition.getIndexOptions(), empty());
    assertThat(indexDefinition.getFields(), hasSize(1));
    IIndexFieldDefinition field = indexDefinition.getFields().get(0);
    assertThat(field.getName(), is(TestMapperIndex.TEXT.getColumnName(mapper)));
  }

  @Entity
  public static class TestMapperNoIndex {
    @Id
    public String id;
    public String text;

  }

  @Entity
  public static class TestMapperIndex {
    public static final IIndexedField TEXT = new IndexedField("text");
    @Id
    public String id;
    public String text;
  }

}
