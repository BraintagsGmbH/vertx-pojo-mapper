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

import org.junit.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper_BeanMethodWithoutField;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestMapping extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestMapping.class);

  @Test
  public void simpleTest(TestContext context) {
    IMapper<MiniMapper> mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
  }

  @Test
  public void testMiniMapper_BeanMethodWithoutField(TestContext context) {
    IMapper<MiniMapper_BeanMethodWithoutField> mapper = getDataStore(context).getMapperFactory()
        .getMapper(MiniMapper_BeanMethodWithoutField.class);
  }

  @Test
  public void testJsonMapping(TestContext context) {
    ObjectMapper mapper = Json.mapper;
    JavaType type = mapper.constructType(MiniMapper.class);
    BeanDescription desc = mapper.getSerializationConfig().introspect(type);

    for (BeanPropertyDefinition def : desc.findProperties()) {
      LOGGER.debug(def);
      LOGGER.debug(def.getFullName());
    }
  }

}
