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
 * Serializer which is used for fields, which are annotated by {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedObjectSerializer extends AbstractDataStoreSerializer<Object> {

  /**
   * Comment for <code>REFERENCE_ID</code>
   */
  public static final String REFERENCE_ID = "referenceId";
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param t
   */
  public ReferencedObjectSerializer(IDataStore datastore) {
    super(datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      JOmnigateGenerator jgen = (JOmnigateGenerator) gen;
      Future<IWriteResult> future = saveReferencedObject(getDatastore(), value);
      String refId = jgen.addEntry(future, false);
      gen.writeString(refId.toString());
    }
  }

}
