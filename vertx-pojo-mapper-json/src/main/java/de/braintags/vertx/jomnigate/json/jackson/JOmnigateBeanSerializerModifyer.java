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
package de.braintags.vertx.jomnigate.json.jackson;

import java.util.Iterator;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Encoder;
import de.braintags.vertx.jomnigate.json.jackson.serializer.EncoderSerializer;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class JOmnigateBeanSerializerModifyer extends BeanSerializerModifier {
  private IDataStore<?, ?> datastore;

  /**
   * 
   */
  public JOmnigateBeanSerializerModifyer(IDataStore<?, ?> datastore) {
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.BeanSerializerModifier#updateBuilder(com.fasterxml.jackson.databind.
   * SerializationConfig, com.fasterxml.jackson.databind.BeanDescription,
   * com.fasterxml.jackson.databind.ser.BeanSerializerBuilder)
   */
  @Override
  public BeanSerializerBuilder updateBuilder(SerializationConfig config, BeanDescription beanDesc,
      BeanSerializerBuilder builder) {
    Iterator<BeanPropertyWriter> it = builder.getProperties().iterator();
    while (it.hasNext()) {
      BeanPropertyWriter p = it.next();
      if (p.getAnnotation(Encoder.class) != null) {
        PropertyName pname = p.getFullName();
        p.assignSerializer(new EncoderSerializer(datastore, beanDesc, pname));
      }
    }
    return super.updateBuilder(config, beanDesc, builder);
  }

}
