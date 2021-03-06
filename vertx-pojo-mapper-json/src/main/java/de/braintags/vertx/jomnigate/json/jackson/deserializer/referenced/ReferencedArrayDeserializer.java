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
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.braintags.vertx.jomnigate.IDataStore;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedArrayDeserializer extends AbstractReferencedDeserializer<Object[]> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param datastore
   * @param beanProperty
   */
  public ReferencedArrayDeserializer(IDataStore datastore, SettableBeanProperty beanProperty) {
    super(datastore, beanProperty);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
   * com.fasterxml.jackson.databind.DeserializationContext)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object[] deserialize(JsonParser p, DeserializationContext ct) throws IOException, JsonProcessingException {
    Object instance = p.getParsingContext().getParent().getCurrentValue();
    JavaType type = getBeanProperty().getType();
    ArrayNode array = p.getCodec().readTree(p);
    Future f = getReferencedObjectsByIdAsArray(array, type.getContentType().getRawClass());
    if (f != null) {
      storePostHandler(ct, instance, f);
    }
    // return null, the real instance will be placed at the end by using the result of the Future
    return null;
  }

}
