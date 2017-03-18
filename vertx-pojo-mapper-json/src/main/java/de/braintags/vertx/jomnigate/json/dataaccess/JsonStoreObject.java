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
package de.braintags.vertx.jomnigate.json.dataaccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateGenerator;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateGenerator.SerializationReference;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedPostHandler;
import de.braintags.vertx.jomnigate.json.mapping.jackson.JacksonMapper;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IStoreObject}, which uses a JsonObject as internal container
 * 
 * @author Michael Remme
 */

public class JsonStoreObject<T> extends AbstractStoreObject<T, JsonObject> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JsonStoreObject.class);

  /**
   * The key, which is used to store the list of referenced composites
   */
  public static final String REFERENCED_LIST = "referencedList";
  private Object generatedId = null;

  /**
   * Constructor
   * 
   * @param mapper
   *          the {@link IMapper} to be used
   * @param entity
   *          the entity to be used
   */
  public JsonStoreObject(IMapper<T> mapper, T entity) {
    super(mapper, entity, new JsonObject());
  }

  /**
   * Constructor
   * 
   * @param jsonObject
   *          the {@link JsonObject} read from the datastore
   * @param mapper
   *          the mapper to be used
   */
  public JsonStoreObject(JsonObject jsonObject, IMapper<T> mapper) {
    super(jsonObject, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractStoreObject#initFromEntity(io.vertx.core.Handler)
   */
  @Override
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    try {
      ObjectMapper mapper = ((JsonDatastore) getMapper().getMapperFactory().getDataStore()).getJacksonMapper();
      SegmentedStringWriter sw = new SegmentedStringWriter(mapper.getFactory()._getBufferRecycler());
      JOmnigateGenerator jgen = (JOmnigateGenerator) mapper.getFactory().createGenerator(sw);
      mapper.writer().writeValue(jgen, getEntity());
      String js = sw.getAndClear();
      replaceReferenced(jgen, js, res -> {
        if (res.failed()) {
          handler.handle(Future.failedFuture(res.cause()));
        } else {
          storeJson(res.result(), handler);
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * @param js
   * @param handler
   */
  private void storeJson(String js, Handler<AsyncResult<Void>> handler) {
    container = new JsonObject(js);
    IProperty idField = getMapper().getIdField();
    container.remove(idField.getName()); // do not write the java fieldname of id, but the column
    Object javaValue = idField.getPropertyAccessor().readData(getEntity());
    put(idField, javaValue == null ? null : String.valueOf(javaValue));

    if (isNewInstance() && getMapper().getKeyGenerator() != null) {
      getNextId(handler);
    } else if (isNewInstance()) {
      getContainer().remove(getMapper().getIdField().getColumnInfo().getName());
      handler.handle(Future.succeededFuture());
    } else {
      handler.handle(Future.succeededFuture());
    }
  }

  private void replaceReferenced(JOmnigateGenerator jgen, String generatedSource,
      Handler<AsyncResult<String>> handler) {
    if (jgen.getReferenceList().isEmpty()) {
      handler.handle(Future.succeededFuture(generatedSource));
    } else {
      CompositeFuture cf = jgen.createComposite();
      cf.setHandler(res -> {
        if (res.failed()) {
          handler.handle(Future.failedFuture(res.cause()));
        } else {
          String newSource = generatedSource;
          try {
            for (SerializationReference ref : jgen.getReferenceList()) {
              newSource = newSource.replace(ref.getReference(), ref.getResolvedReference());
            }
            handler.handle(Future.succeededFuture(newSource));
          } catch (Exception e) {
            handler.handle(Future.failedFuture(e));
          }
        }
      });
    }

  }

  /**
   * The implementation is calling jackson serializer first and afterwards it is calling single handlers
   */
  @Override
  public void initToEntity(Handler<AsyncResult<Void>> handler) {
    try {
      IProperty idField = getMapper().getIdField();
      String id = (String) getContainer().remove(idField.getColumnInfo().getName());
      getContainer().put(idField.getName(), id);
      doMapping(res -> {
        if (res.failed()) {
          handler.handle(Future.failedFuture(res.cause()));
        } else {
          finishToEntity(res.result(), handler);
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private void doMapping(Handler<AsyncResult<T>> handler) throws IOException {
    ObjectMapper mapper = ((JsonDatastore) getMapper().getMapperFactory().getDataStore()).getJacksonMapper();
    List<ReferencedPostHandler> valueList = new ArrayList<>();
    InjectableValues iv = new InjectableValues.Std().addValue(REFERENCED_LIST, valueList);
    ObjectReader reader = mapper.reader(iv);
    T instance = reader.forType(((JacksonMapper<T>) getMapper()).getCreatorClass()).readValue(getContainer().encode());
    if (!valueList.isEmpty()) {
      prehandleReferenced(handler, instance, valueList);
    } else {
      handler.handle(Future.succeededFuture(instance));
    }
  }

  /**
   * Referenced instances were loaded async way, now its time to place them into the instance
   */
  private void prehandleReferenced(Handler<AsyncResult<T>> handler, T mainInstance,
      final List<ReferencedPostHandler> valueList) {
    List<Future> fl = new ArrayList<>();
    valueList.stream().forEach(rp -> fl.add(rp.getFuture()));
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        // all futures for referenced instances are ready, now set the values
        setReferencedValues(handler, mainInstance, valueList);
      }
    });
  }

  private void setReferencedValues(Handler<AsyncResult<T>> handler, T mainInstance,
      final List<ReferencedPostHandler> valueList) {
    try {
      for (ReferencedPostHandler rp : valueList) {
        if (rp.getInstance() != null) {
          Object fieldValue = rp.getFuture().result();
          rp.getBeanProperty().set(rp.getInstance(), fieldValue);
        }
      }
      handler.handle(Future.succeededFuture(mainInstance));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IStoreObject#get(de.braintags.vertx.jomnigate.mapping.IField)
   */
  @Override
  public Object get(IProperty field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().getValue(colName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mapping.IStoreObject#hasProperty(de.braintags.vertx.jomnigate.mapping.IField)
   */
  @Override
  public boolean hasProperty(IProperty field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().containsKey(colName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IStoreObject#put(de.braintags.vertx.jomnigate.mapping.IField,
   * java.lang.Object)
   */
  @Override
  public IStoreObject<T, JsonObject> put(IProperty field, Object value) {
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      throw new MappingException("Can't find columninfo for field " + field.getFullName());
    }
    if (field.isIdField() && value != null) {
      setNewInstance(false);
    }
    if (value == null) {
      getContainer().putNull(ci.getName());
    } else {
      getContainer().put(ci.getName(), value);
    }
    return this;
  }

  /**
   * In case of a defined {@link IKeyGenerator} the next id is requested for a new record
   * 
   * @param handler
   */
  public void getNextId(Handler<AsyncResult<Void>> handler) {
    IKeyGenerator gen = getMapper().getKeyGenerator();
    gen.generateKey(getMapper(), keyResult -> {
      if (keyResult.failed()) {
        handler.handle(Future.failedFuture(keyResult.cause()));
      } else {
        generatedId = keyResult.result().getKey();
        IProperty field = getMapper().getIdField();
        this.put(field, String.valueOf(generatedId));
        setNewInstance(true);
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Get a generated id
   * 
   * @return an instance, if a new id was generated for a new record or null
   */
  public Object getGeneratedId() {
    return generatedId;
  }

}
