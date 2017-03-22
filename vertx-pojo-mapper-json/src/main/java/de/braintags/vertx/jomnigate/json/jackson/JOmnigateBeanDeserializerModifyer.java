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
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.AbstractReferencedDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedArrayDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedCollectionDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedMapDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedObjectDeserializer;

/**
 * For {@link Referenced} we need special deserializer, which gain a SettableBeanProperty to set the field content at
 * the end, when referenced instances were loaded async
 * 
 * @author Michael Remme
 * 
 */
public class JOmnigateBeanDeserializerModifyer extends BeanDeserializerModifier {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JOmnigateBeanDeserializerModifyer.class);
  private IDataStore datastore;

  public JOmnigateBeanDeserializerModifyer(IDataStore datastore) {
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.deser.BeanDeserializerModifier#updateBuilder(com.fasterxml.jackson.databind.
   * DeserializationConfig, com.fasterxml.jackson.databind.BeanDescription,
   * com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder)
   */
  @Override
  public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
      BeanDeserializerBuilder builder) {
    Iterator<SettableBeanProperty> it = builder.getProperties();
    while (it.hasNext()) {
      SettableBeanProperty p = it.next();
      if (p.getAnnotation(Referenced.class) != null) {
        builder.addOrReplaceProperty(p.withValueDeserializer(findReferencedDeserializer(builder, beanDesc, p)), true);
      }
    }
    return builder;
  }

  /**
   * @param p
   * @return
   */
  private AbstractReferencedDeserializer findReferencedDeserializer(BeanDeserializerBuilder builder,
      BeanDescription beanDesc, SettableBeanProperty p) {
    JavaType jt = p.getType();
    LOGGER.debug("finding deserializer for " + p + " : " + jt);
    if (jt.isArrayType()) {
      return new ReferencedArrayDeserializer(datastore, p);
    } else if (jt.isMapLikeType()) {
      return new ReferencedMapDeserializer(datastore, p);
    } else if (jt.isCollectionLikeType()) {
      return new ReferencedCollectionDeserializer(datastore, p);
    } else if (jt.isEnumType()) {
      throw new UnsupportedOperationException("referenced Enum is not supported");
    } else {
      return new ReferencedObjectDeserializer(datastore, p);
    }
  }

}
