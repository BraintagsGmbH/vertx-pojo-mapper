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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.impl.AbstractDataStore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.JacksonModuleJomnigate;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * an abstract implementation of {@link IDataStore} which uses jackson as mapper base
 * 
 * @author Michael Remme
 * 
 */
public abstract class JsonDatastore extends AbstractDataStore {
  private ObjectMapper jacksonMapper;
  private ObjectMapper jacksonPrettyMapper = new ObjectMapper();

  /**
   * @param vertx
   * @param properties
   */
  public JsonDatastore(Vertx vertx, JsonObject properties) {
    super(vertx, properties);
    JOmnigateFactory jf = new JOmnigateFactory(Json.mapper.getFactory(), Json.mapper);
    jacksonMapper = new ObjectMapper(jf);
    jf = new JOmnigateFactory(Json.prettyMapper.getFactory(), Json.prettyMapper);
    jacksonPrettyMapper = new ObjectMapper(jf);

    // Non-standard JSON but we allow C style comments in our JSON
    jacksonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    jacksonPrettyMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    jacksonPrettyMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    jacksonMapper.registerModule(new JacksonModuleJomnigate(this));
    jacksonMapper.registerModule(new JodaModule());
    jacksonMapper.registerModule(new GuavaModule());

    jacksonPrettyMapper.registerModule(new JacksonModuleJomnigate(this));
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
