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

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.impl.AbstractDataStore;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.JacksonModuleJomnigate;
import de.braintags.vertx.jomnigate.mapping.IDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
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
  private final ObjectMapper jacksonPrettyMapper;

  /**
   * @param vertx
   * @param properties
   */
  public JsonDatastore(Vertx vertx, JsonObject properties, DataStoreSettings settings) {
    super(vertx, properties, settings);
    JOmnigateFactory jf = new JOmnigateFactory(this, Json.mapper.getFactory(), Json.mapper);
    jacksonMapper = new ObjectMapper(jf);
    jacksonMapper.setFilterProvider(Json.mapper.getSerializationConfig().getFilterProvider());
    jf = new JOmnigateFactory(this, Json.prettyMapper.getFactory(), Json.prettyMapper);
    jacksonPrettyMapper = new ObjectMapper(jf);
    jacksonPrettyMapper.setFilterProvider(Json.prettyMapper.getSerializationConfig().getFilterProvider());

    // Non-standard JSON but we allow C style comments in our JSON
    jacksonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    jacksonPrettyMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    jacksonPrettyMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    jacksonMapper.registerModule(new JacksonModuleJomnigate(this));
    jacksonMapper.registerModule(new ParameterNamesModule(Mode.DEFAULT));
    jacksonMapper.registerModule(new JodaModule());
    jacksonMapper.registerModule(new GuavaModule());

    jacksonPrettyMapper.registerModule(new JacksonModuleJomnigate(this));
    jacksonPrettyMapper.registerModule(new ParameterNamesModule(Mode.DEFAULT));
    jacksonPrettyMapper.registerModule(new JodaModule());
    jacksonPrettyMapper.registerModule(new GuavaModule());
  }

  /**
   * The jackson mapper which is used to serialize and deserialize instances
   * 
   * @return the jacksonMapper
   */
  public ObjectMapper getJacksonMapper() {
    return jacksonMapper;
  }

  /**
   * The jackson mapper which is used to serialize and deserialize instances as pretty source
   * 
   * @return the jacksonPrettyMapper
   */
  public ObjectMapper getJacksonPrettyMapper() {
    return jacksonPrettyMapper;
  }

}
