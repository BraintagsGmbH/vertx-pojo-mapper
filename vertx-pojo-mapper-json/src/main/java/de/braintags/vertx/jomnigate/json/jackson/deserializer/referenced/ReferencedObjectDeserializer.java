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
package de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import io.vertx.core.Future;

/**
 * Deserializer to deserialize fields which are annotated as {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedObjectDeserializer extends AbstractReferencedDeserializer<Object> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param datastore
   * @param beanProperty
   */
  public ReferencedObjectDeserializer(IDataStore datastore, SettableBeanProperty beanProperty) {
    super(datastore, beanProperty);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
   * com.fasterxml.jackson.databind.DeserializationContext)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public Object deserialize(JsonParser p, DeserializationContext ct) throws IOException, JsonProcessingException {
    JavaType type = getBeanProperty().getType();
    Object instance = p.getCurrentValue();
    JsonNode node = p.getCodec().readTree(p);
    String id = node.asText();
    Future f = getReferencedObjectById(id, type.getRawClass());
    storePostHandler(ct, instance, f);
    // return null, the real instance will be placed at the end by using the result of the Future
    return null;
  }

}
