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
package de.braintags.vertx.jomnigate.json.jackson.serializer.referenced;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateGenerator;
import de.braintags.vertx.jomnigate.json.jackson.serializer.AbstractDataStoreSerializer;
import io.vertx.core.Future;

/**
 * Serializer which is used for fields, which are annotated by {@link Referenced} with a type of an Array.
 * Note: extension of standard ArraySerializer from jackson, by adding a type handler to ReferencedObject, is not
 * choosen, to enable saving and loading of items as one list.
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedArraySerializer extends AbstractDataStoreSerializer<Object[]> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param datastore
   */
  public ReferencedArraySerializer(IDataStore datastore) {
    super(datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(Object[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeStartArray();
      if (value.length > 0) {
        JOmnigateGenerator jgen = (JOmnigateGenerator) gen;
        Future<IWriteResult> future = saveReferencedObjects(getDatastore(), value);
        String refId = jgen.addEntry(future, true);
        gen.writeString(refId.toString());
      }
      gen.writeEndArray();
    }
  }

}
