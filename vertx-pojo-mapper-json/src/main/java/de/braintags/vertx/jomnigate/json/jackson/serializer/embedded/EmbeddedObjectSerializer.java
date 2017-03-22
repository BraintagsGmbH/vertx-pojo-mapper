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
package de.braintags.vertx.jomnigate.json.jackson.serializer.embedded;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateGenerator;
import de.braintags.vertx.jomnigate.json.jackson.serializer.AbstractDataStoreSerializer;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@SuppressWarnings("serial")
public class EmbeddedObjectSerializer extends AbstractDataStoreSerializer<Object> {
  private JsonSerializer parentSerializer;

  /**
   * @param datastore
   */
  public EmbeddedObjectSerializer(IDataStore datastore, BeanDescription beanDesc, BeanPropertyWriter beanProperty) {
    super(datastore);
    parentSerializer = beanProperty.getSerializer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    if (value != null) {
      IMapper mapper = getDatastore().getMapperFactory().getMapper(value.getClass());
      if (isNewRecord(mapper, value)) {
        JOmnigateGenerator jgen = (JOmnigateGenerator) gen;
        Future<Object> future = generateKey(mapper, value);
        String refId = jgen.addEntry(future);
        gen.writeString(refId.toString());
      } else {
        parentSerializer.serialize(value, gen, provider);
      }
    } else {
      parentSerializer.serialize(value, gen, provider);
    }
  }

  private boolean isNewRecord(IMapper mapper, Object entity) {
    IProperty idProp = mapper.getIdField().getField();
    return idProp.getPropertyAccessor().readData(entity) == null;
  }

  /**
   * generates a new id for the given entity and packs the entity into the result
   * 
   * @param mapper
   * @param entity
   * @return
   */
  private Future<Object> generateKey(IMapper mapper, Object entity) {
    Future<Object> f = Future.future();
    mapper.getKeyGenerator().generateKey(mapper, keyResult -> {
      if (keyResult.failed()) {
        f.fail(keyResult.cause());
      } else {
        IProperty field = mapper.getIdField().getField();
        field.getPropertyAccessor().writeData(entity, String.valueOf(keyResult.result().getKey()));
        f.complete(entity);
      }
    });
    return f;
  }

}
