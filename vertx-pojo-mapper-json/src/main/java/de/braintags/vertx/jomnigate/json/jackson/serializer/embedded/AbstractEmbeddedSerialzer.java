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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.jackson.serializer.AbstractDataStoreSerializer;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import io.vertx.core.Future;

/**
 * Serializer to handle properties, which are annotated with {@link Embedded}. For new instance, it generates an ID and
 * adds it into the record before serializing the instance
 * 
 * @author Michael Remme
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractEmbeddedSerialzer<T> extends AbstractDataStoreSerializer<T> {
  private final IMapper<?> mapper;

  /**
   * 
   * @param datastore
   * @param annotated
   */
  public AbstractEmbeddedSerialzer(final IDataStore datastore, final Class mapperClass) {
    super(datastore);
    this.mapper = initMapper(datastore, mapperClass);
  }

  /**
   * 
   * @param datastore
   * @param beanDesc
   * @param beanProperty
   */
  public AbstractEmbeddedSerialzer(final IDataStore datastore, final BeanDescription beanDesc, final BeanPropertyWriter beanProperty) {
    super(datastore);
    this.mapper = initMapper(datastore, beanProperty);
  }

  /**
   * Init the IMapper instance from the value class
   * 
   * @param datastore
   * @param beanProperty
   */
  protected IMapper initMapper(final IDataStore datastore, final Class mapperClass) {
    IMapper<?> m = datastore.getMapperFactory().getMapper(mapperClass);
    if (m.getKeyGenerator() == null) {
      throw new MappingException(
          "Mapper " + m.getMapperClass().getName() + " is used as embedded and needs a defined KeyGenerator");
    }
    return m;
  }

  /**
   * Init the IMapper instance from the value class
   * 
   * @param datastore
   * @param beanProperty
   */
  protected IMapper initMapper(final IDataStore datastore, final BeanPropertyWriter beanProperty) {
    IMapper<?> m = datastore.getMapperFactory().getMapper(beanProperty.getType().getRawClass());
    if (m.getKeyGenerator() == null) {
      throw new MappingException(
          "Mapper " + m.getMapperClass().getName() + " is used as embedded and needs a defined KeyGenerator");
    }
    return m;
  }

  protected final boolean isNewRecord(final IMapper mapper, final Object entity) {
    IProperty idProp = mapper.getIdInfo().getField();
    return idProp.getPropertyAccessor().readData(entity) == null;
  }

  /**
   * generates a new id for the given entity and packs the entity into the result
   * 
   * @param mapper
   * @param entity
   * @return
   */
  protected final Future<Object> generateKey(final IMapper mapper, final Object entity) {
    Future<Object> f = Future.future();
    mapper.getKeyGenerator().generateKey(mapper, keyResult -> {
      if (keyResult.failed()) {
        f.fail(keyResult.cause());
      } else {
        IProperty field = mapper.getIdInfo().getField();
        field.getPropertyAccessor().writeData(entity, String.valueOf(keyResult.result().getKey()));
        f.complete(entity);
      }
    });
    return f;
  }

  /**
   * @return the mapper
   */
  public IMapper getMapper() {
    return mapper;
  }

}
