/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.impl.AbstractDataStore;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.JacksonModuleJomnigate;
import de.braintags.vertx.jomnigate.mapping.IDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
import de.braintags.vertx.util.json.JsonConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * an abstract implementation of {@link IDataStore} which uses jackson as mapper base
 *
 * @author Michael Remme
 *
 * @param <S>
 *          the type of the {@link IStoreObjectFactory}
 * @param <U>
 *          the format used by the underlaing {@link IDataStoreSynchronizer}
 */
public abstract class JsonDatastore extends AbstractDataStore<JsonObject, JsonObject> {
  private final ObjectMapper jacksonMapper;

  private final Map<Class<?>, ObjectMapper> viewMapper = new ConcurrentHashMap<>();

  /**
   * @param vertx
   * @param properties
   */
  public JsonDatastore(final Vertx vertx, final JsonObject properties, final DataStoreSettings settings) {
    super(vertx, properties, settings);
    // Do not change factory type, it is used by the @link{JomnigateJsonModule} to detect jOmnigate environment
    JOmnigateFactory jOmnigateFactory = new JOmnigateFactory(this, Json.mapper.getFactory(), Json.mapper);
    jacksonMapper = new ObjectMapper(jOmnigateFactory);
    jacksonMapper.registerModule(new JacksonModuleJomnigate(this));
    jacksonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    JsonConfig.configureObjectMapper(jacksonMapper);
  }

  /**
   * The jackson mapper which is used to serialize and deserialize instances
   *
   * @return the jacksonMapper
   */
  public ObjectMapper getJacksonMapper() {
    return jacksonMapper;
  }

  public ObjectMapper getMapperForView(final Class<?> viewClass) {
    if (viewClass == null) {
      return getJacksonMapper();
    }
    return viewMapper.computeIfAbsent(viewClass, requestedViewClass -> {
      ObjectMapper mapper = getJacksonMapper().copy();
      mapper.setConfig(mapper.getSerializationConfig().withView(requestedViewClass));
      return mapper;
    });
  }

}
