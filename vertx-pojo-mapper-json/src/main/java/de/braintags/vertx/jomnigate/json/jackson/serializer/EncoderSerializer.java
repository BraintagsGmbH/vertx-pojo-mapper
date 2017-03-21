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
package de.braintags.vertx.jomnigate.json.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Encoder;
import de.braintags.vertx.util.security.crypt.IEncoder;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class EncoderSerializer extends AbstractDataStoreSerializer {
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;
  private IEncoder encoder;
  private AnnotatedMember annotated;

  /**
   * @param datastore
   */
  public EncoderSerializer(IDataStore datastore, Annotated am) {
    super(datastore);
    if (am instanceof AnnotatedMember) {
      annotated = (AnnotatedMember) am;
    } else {
      throw new UnsupportedOperationException("Need an instance of AnnotatedMember");
    }
    computeEncoder(am);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    String encoded = encoder.encode((CharSequence) value);
    Object current = gen.getCurrentValue();
    gen.writeString(encoded);
    // annotated.getType().

    annotated.setValue(current, encoded);
  }

  private void computeEncoder(Annotated am) {
    String encoderName = am.getAnnotation(Encoder.class).name();
    IEncoder enc = getDatastore().getEncoder(encoderName);
    if (enc == null) {
      throw new UnsupportedOperationException("The encoder with name " + encoderName
          + " does not exist. You need to add it into the datastore. Field: " + getFullName(am));
    }
    if (am.getType().isTypeOrSubTypeOf(CharSequence.class)) {
      this.encoder = enc;
    } else {
      throw new UnsupportedOperationException(
          "Annotation Encoded can only be used for fields instance of CharSequence. Field: " + getFullName(am));
    }
  }

}
