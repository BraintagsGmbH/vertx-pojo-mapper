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
package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class ReferenceMapper_List {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(ReferenceMapper_List.class);

  @Id
  public String id;
  @Referenced
  public List<SimpleMapper> simpleMapper;

  /**
   * 
   */
  public ReferenceMapper_List() {
    simpleMapper = new ArrayList<SimpleMapper>();
    for (int i = 0; i < 5; i++) {
      simpleMapper.add(new SimpleMapper("name " + i, "sec prop " + i));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    Assert.assertEquals(getClass(), obj.getClass());
    ReferenceMapper_List compare = (ReferenceMapper_List) obj;
    Assert.assertEquals("IDs are not equal", id, compare.id);
    Assert.assertEquals("number of elements", simpleMapper.size(), compare.simpleMapper.size());
    for (SimpleMapper sm : simpleMapper) {
      if (!compare.hasSimpleMapperById(sm))
        Assert.fail("there is a list element missing");
    }
    LOGGER.info("all elements in rthe list are contained");
    // now checking for the list
    for (int i = 0; i < simpleMapper.size(); i++) {
      SimpleMapper mapper = simpleMapper.get(i);
      SimpleMapper compareSimpleMapper = compare.simpleMapper.get(i);
      if (!mapper.equals(compareSimpleMapper))
        LOGGER.warn("sorting of referenced list is different");
    }

    return true;
  }

  boolean hasSimpleMapperById(SimpleMapper mapper) {
    for (SimpleMapper compare : simpleMapper) {
      if (compare.id.equals(mapper.id))
        return true;
    }
    return false;
  }

}
