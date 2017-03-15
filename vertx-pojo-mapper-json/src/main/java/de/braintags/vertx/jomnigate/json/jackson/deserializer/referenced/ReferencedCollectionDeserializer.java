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
import java.util.ArrayList;
import java.util.Collection;

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
public class ReferencedCollectionDeserializer extends AbstractReferencedDeserializer<Collection<?>> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param datastore
   * @param beanProperty
   */
  public ReferencedCollectionDeserializer(IDataStore datastore, SettableBeanProperty beanProperty) {
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
  public Collection<?> deserialize(JsonParser p, DeserializationContext ct)
      throws IOException, JsonProcessingException {
    JavaType type = getBeanProperty().getType();
    ArrayNode array = p.getCodec().readTree(p);
    Collection collection = null;
    if (array != null && array.size() > 0) {
      collection = instantiate(ct, type);
      Future f = getReferencedObjectsById(array, type.getContentType().getRawClass(), collection);
      if (f != null) {
        storePostHandler(ct, f);
      }
    }
    return collection;
  }

  @Override
  protected Collection instantiateInternal(DeserializationContext ct, JavaType type) {
    return new ArrayList<>();
  }

}
