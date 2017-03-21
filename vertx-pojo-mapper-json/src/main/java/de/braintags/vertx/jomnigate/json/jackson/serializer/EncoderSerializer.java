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
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

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
  private AnnotatedMember setter;

  public EncoderSerializer(IDataStore datastore, BeanDescription beanDesc, PropertyName propertyName) {
    super(datastore);
    List<BeanPropertyDefinition> propertyList = beanDesc.findProperties();
    for (BeanPropertyDefinition def : propertyList) {
      if (def.getFullName().equals(propertyName)) {
        setter = def.getMutator();
        String encoder = def.getAccessor().getAnnotation(Encoder.class).name();
        computeEncoder(encoder, def.getAccessor().getType(), propertyName.getSimpleName());
      }
    }
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
    setter.setValue(current, encoded);
  }

  private void computeEncoder(String encoderName, JavaType type, String fieldName) {
    IEncoder enc = getDatastore().getEncoder(encoderName);
    if (enc == null) {
      throw new UnsupportedOperationException(
          "The encoder with name " + encoderName + " does not exist. You need to add it into the datastore. Field: "
              + type.getRawClass().getName() + "." + fieldName);
    }
    if (type.isTypeOrSubTypeOf(CharSequence.class)) {
      this.encoder = enc;
    } else {
      throw new UnsupportedOperationException(
          "Annotation Encoded can only be used for fields instance of CharSequence. Field: "
              + type.getRawClass().getName() + "." + fieldName);
    }
  }

}
