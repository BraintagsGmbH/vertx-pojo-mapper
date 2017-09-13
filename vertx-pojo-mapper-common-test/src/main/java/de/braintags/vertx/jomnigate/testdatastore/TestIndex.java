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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.testdatastore.mapper.GeoMapper2;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperIndex;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperIndexPartialFilter;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperIndexUnique;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapperIndexUniqueFilter;
import io.vertx.ext.unit.TestContext;

/**
 * Test indexing of fields
 * 
 * 
 * @author Michael Remme
 *
 */
public class TestIndex extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestIndex.class);

  @Test
  public void testGeoIndex(final TestContext context) {
    clearTable(context, GeoMapper2.class.getSimpleName());
    IQuery<GeoMapper2> q = getDataStore(context).createQuery(GeoMapper2.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), getIndexDefinition(GeoMapper2.class, context));
  }

  @Test
  public void testIndexMiniMapper(final TestContext context) {
    clearTable(context, MiniMapperIndex.class.getSimpleName());
    IQuery<MiniMapperIndex> q = getDataStore(context).createQuery(MiniMapperIndex.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), getIndexDefinition(MiniMapperIndex.class, context));
  }

  @Test
  public void testIndexMiniMapper_partialFilter(final TestContext context) {
    clearTable(context, MiniMapperIndexPartialFilter.class.getSimpleName());
    IQuery<MiniMapperIndexPartialFilter> q = getDataStore(context).createQuery(MiniMapperIndexPartialFilter.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), getIndexDefinition(MiniMapperIndexPartialFilter.class, context));
  }

  @Test
  public void testIndexMiniMapper_unique(final TestContext context) {
    clearTable(context, MiniMapperIndexUnique.class.getSimpleName());
    IQuery<MiniMapperIndexUnique> q = getDataStore(context).createQuery(MiniMapperIndexUnique.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), getIndexDefinition(MiniMapperIndexUnique.class, context));
  }

  @Test
  public void testIndexMiniMapper_unique_filter(final TestContext context) {
    clearTable(context, MiniMapperIndexUniqueFilter.class.getSimpleName());
    IQuery<MiniMapperIndexUniqueFilter> q = getDataStore(context).createQuery(MiniMapperIndexUniqueFilter.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), getIndexDefinition(MiniMapperIndexUniqueFilter.class, context));
    MiniMapperIndexUniqueFilter unfiltered1 = new MiniMapperIndexUniqueFilter(false);
    unfiltered1.name = "not-unique";
    MiniMapperIndexUniqueFilter unfiltered2 = new MiniMapperIndexUniqueFilter(false);
    unfiltered2.name = "not-unique";
    // should not throw exception, as they don't match the filter query
    saveRecords(context, Arrays.asList(unfiltered1, unfiltered2));

    MiniMapperIndexUniqueFilter filtered1 = new MiniMapperIndexUniqueFilter(true);
    filtered1.name = "unique";
    saveRecord(context, filtered1);

    MiniMapperIndexUniqueFilter filtered2 = new MiniMapperIndexUniqueFilter(true);
    filtered2.name = "unique";
    IWrite<MiniMapperIndexUniqueFilter> write = getDataStore(context).createWrite(MiniMapperIndexUniqueFilter.class);
    write.add(filtered2);
    write.save(context.asyncAssertFailure(e -> assertThat(e, instanceOf(DuplicateKeyException.class))));
  }

  @BeforeClass
  public static void beforeClass(final TestContext context) {
    dropTable(context, GeoMapper2.class.getSimpleName());
  }

  private IIndexDefinition getIndexDefinition(final Class<?> mapperClass, final TestContext context) {
    ImmutableSet<IIndexDefinition> indexDefinitions = getDataStore(context).getMapperFactory().getMapper(mapperClass)
        .getIndexDefinitions();
    assertThat(indexDefinitions, hasSize(1));
    IIndexDefinition indexDefinition = indexDefinitions.iterator().next();
    return indexDefinition;
  }
}
