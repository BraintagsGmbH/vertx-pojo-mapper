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
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.serializer.JOmnigateGenerator;
import io.vertx.core.Future;

/**
 * Serializer to handle properties, which are annotated with {@link Embedded}. For new isntance, it generates an ID and
 * adds it into the record before serializing the instance
 * 
 * @author Michael Remme
 * 
 */
@SuppressWarnings("serial")
public class EmbeddedObjectSerializer extends AbstractEmbeddedSerialzer<Object> {

  /**
   * @param datastore
   * @param annotated
   */
  public EmbeddedObjectSerializer(IDataStore datastore, Class mapperClass) {
    super(datastore, mapperClass);
  }

  /**
   * 
   * @param datastore
   * @param beanDesc
   * @param beanProperty
   */
  public EmbeddedObjectSerializer(IDataStore datastore, BeanDescription beanDesc, BeanPropertyWriter beanProperty) {
    super(datastore, beanDesc, beanProperty);
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
      if (isNewRecord(getMapper(), value)) {
        JOmnigateGenerator jgen = (JOmnigateGenerator) gen;
        Future<Object> future = generateKey(getMapper(), value);
        String refId = jgen.addEntry(future);
        gen.writeString(refId.toString());
      } else {
        ((JsonDatastore) getDatastore()).getJacksonMapper().writerFor(Object.class).writeValue(gen, value);
      }
    } else {
      ((JsonDatastore) getDatastore()).getJacksonMapper().writerFor(Object.class).writeValue(gen, value);
    }
  }

}
