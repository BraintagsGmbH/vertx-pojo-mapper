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
package de.braintags.vertx.jomnigate.json.mapping.jackson;

import org.apache.commons.lang3.ClassUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.dataaccess.query.impl.GeoSearchArgument;
import de.braintags.vertx.jomnigate.exception.QueryParameterException;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.util.exception.PropertyAccessException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IPropertyMapper} for use with jackson
 * 
 * @author Michael Remme
 * 
 */
public class JacksonPropertyMapper implements IPropertyMapper {
  private ObjectMapper objectMapper;

  public JacksonPropertyMapper(JsonDatastore datastore) {
    this.objectMapper = datastore.getJacksonMapper();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#convertForStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IProperty, io.vertx.core.Handler)
   */
  @Override
  public <T> void convertForStore(T value, IProperty field, Handler<AsyncResult<Object>> handler) {
    try {
      Object transformedValue;
      if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
        transformedValue = value;
      } else if (value instanceof CharSequence) {
        transformedValue = value.toString();
      } else if (value instanceof Enum) {
        transformedValue = ((Enum<?>) value).name();
      } else if (value instanceof GeoSearchArgument) {
        transformedValue = new JsonObject(Json.encode(value));
      } else {
        // can not use datastore object mapper here because only the JSON datastore has the object mapper
        transformedValue = Json.encode(value);
      }
      handler.handle(Future.succeededFuture(transformedValue));
    } catch (Exception e) {
      handler.handle(
          Future.failedFuture(new QueryParameterException("Unable to transform complex object for value " + value, e)));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#intoStoreObject(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IStoreObject, de.braintags.vertx.jomnigate.mapping.IProperty,
   * io.vertx.core.Handler)
   */
  @Override
  public <T> void intoStoreObject(T entity, IStoreObject<T, ?> storeObject, IProperty field,
      Handler<AsyncResult<Void>> handler) {
    try {
      IPropertyAccessor pAcc = field.getPropertyAccessor();
      Object javaValue = pAcc.readData(entity);
      if (field.getEncoder() != null) {
        javaValue = field.getEncoder().encode((CharSequence) javaValue);
        pAcc.writeData(entity, javaValue);
      }
      String converted = objectMapper.writeValueAsString(javaValue);
      storeObject.put(field, converted);
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#readForStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IProperty, io.vertx.core.Handler)
   */
  @Override
  public <T> void readForStore(T entity, IProperty field, Handler<AsyncResult<Object>> handler) {
    try {
      IPropertyAccessor pAcc = field.getPropertyAccessor();
      Object javaValue = pAcc.readData(entity);
      String converted = objectMapper.writeValueAsString(javaValue);
      handler.handle(Future.succeededFuture(converted));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#fromStoreObject(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IStoreObject, de.braintags.vertx.jomnigate.mapping.IProperty,
   * io.vertx.core.Handler)
   */
  @Override
  public <T> void fromStoreObject(T entity, IStoreObject<T, ?> storeObject, IProperty field,
      Handler<AsyncResult<Void>> handler) {
    try {
      String dbValue = (String) storeObject.get(field);
      Object javaValue = objectMapper.readValue(dbValue,
          ((JacksonProperty) field).getBeanPropertyDefinition().getAccessor().getType());
      handleInstanceFromStore(storeObject, entity, javaValue, dbValue, field, handler);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private <T> void handleInstanceFromStore(IStoreObject<T, ?> storeObject, T mapper, Object javaValue, Object dbValue,
      IProperty field, Handler<AsyncResult<Void>> handler) {
    try {
      if (javaValue instanceof IObjectReference) {
        storeObject.getObjectReferences().add((IObjectReference) javaValue);
      } else {
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(mapper, javaValue);
      }
      handler.handle(Future.succeededFuture());
    } catch (PropertyAccessException e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#fromObjectReference(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IObjectReference, io.vertx.core.Handler)
   */
  @Override
  public void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException("this should not land here for jackson")));
  }

}
