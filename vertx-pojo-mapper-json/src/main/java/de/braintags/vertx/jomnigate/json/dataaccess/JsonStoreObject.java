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

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.JOmnigateFactory;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced.ReferencedPostHandler;
import de.braintags.vertx.jomnigate.json.jackson.serializer.JOmnigateGenerator;
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
 * @param <T>
 *          the type of the entity
 */

public class JsonStoreObject<T> extends AbstractStoreObject<T, JsonObject> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JsonStoreObject.class);

  /**
   * The key, which is used to store the list of referenced composites
   */
  public static final String REFERENCED_LIST = "referencedList";
  private Object generatedId = null;

  private final Class<?> view;

  /**
   * Constructor
   *
   * @param mapper
   *          the {@link IMapper} to be used
   * @param entity
   *          the entity to be used
   */
  public JsonStoreObject(final IMapper<T> mapper, final T entity) {
    super(mapper, entity, new JsonObject());
    this.view = null;
  }

  /**
   * Constructor
   *
   * @param mapper
   *          the {@link IMapper} to be used
   * @param entity
   *          the entity to be used
   * @view view used to specifiy special jsonview during serialization
   */
  public JsonStoreObject(final IMapper<T> mapper, final T entity, final Class<?> view) {
    super(mapper, entity, new JsonObject());
    this.view = view;
  }

  /**
   * Constructor
   *
   * @param jsonObject
   *          the {@link JsonObject} read from the datastore
   * @param mapper
   *          the mapper to be used
   */
  public JsonStoreObject(final JsonObject jsonObject, final IMapper<T> mapper) {
    super(jsonObject, mapper);
    view = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractStoreObject#initFromEntity(io.vertx.core.Handler)
   */
  @Override
  public void initFromEntity(final Handler<AsyncResult<Void>> handler) {
    try {
      JsonDatastore datastore = (JsonDatastore) getMapper().getMapperFactory().getDataStore();

      JOmnigateGenerator jgen = JOmnigateFactory.createGenerator(datastore);
      ObjectMapper mapper = datastore.getMapperForView(view);
      mapper.writer().writeValue(jgen, getEntity());
      jgen.getResult(res -> {
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
  private void storeJson(final String js, final Handler<AsyncResult<Void>> handler) {
    try {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Storing json: " + js);
      }
      container = new JsonObject(js);
      IProperty idField = getMapper().getIdInfo().getField();
      container.remove(idField.getName()); // do not write the java fieldname of id, but the column
      Object javaValue = idField.getPropertyAccessor().readData(getEntity());
      put(idField, javaValue == null ? null : String.valueOf(javaValue));

      if (isNewInstance() && getMapper().getKeyGenerator() != null) {
        getNextId(handler);
      } else if (isNewInstance()) {
        getContainer().remove(getMapper().getIdInfo().getField().getColumnInfo().getName());
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.succeededFuture());
      }
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * The implementation is calling jackson serializer first and afterwards it is calling single handlers
   */
  @Override
  public void initToEntity(final Handler<AsyncResult<Void>> handler) {
    IProperty idField = getMapper().getIdInfo().getField();
    String id = (String) getContainer().remove(idField.getColumnInfo().getName());
    getContainer().put(idField.getName(), id);
    doMapping(res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        finishToEntity(res.result(), handler);
      }
    });
  }

  private void doMapping(final Handler<AsyncResult<T>> handler) {
    ObjectMapper mapper = ((JsonDatastore) getMapper().getMapperFactory().getDataStore()).getJacksonMapper();
    List<ReferencedPostHandler> valueList = new ArrayList<>();
    InjectableValues iv = new InjectableValues.Std().addValue(REFERENCED_LIST, valueList);
    ObjectReader reader = mapper.reader(iv);
    T instance;
    try {
      instance = reader.forType(((JacksonMapper<T>) getMapper()).getCreatorClass())
          .readValue(mapper.<JsonNode> valueToTree(getContainer().getMap()));
    } catch (IOException e) {
      handler.handle(Future.failedFuture(e));
      return;
    }
    if (!valueList.isEmpty()) {
      prehandleReferenced(handler, instance, valueList);
    } else {
      handler.handle(Future.succeededFuture(instance));
    }
  }

  /**
   * Referenced instances were loaded async way, now its time to place them into the instance
   */
  private void prehandleReferenced(final Handler<AsyncResult<T>> handler, final T mainInstance,
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

  private void setReferencedValues(final Handler<AsyncResult<T>> handler, final T mainInstance,
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
  public Object get(final IProperty field) {
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
  public boolean hasProperty(final IProperty field) {
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
  public IStoreObject<T, JsonObject> put(final IProperty field, final Object value) {
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
  public void getNextId(final Handler<AsyncResult<Void>> handler) {
    IKeyGenerator gen = getMapper().getKeyGenerator();
    gen.generateKey(getMapper(), keyResult -> {
      if (keyResult.failed()) {
        handler.handle(Future.failedFuture(keyResult.cause()));
      } else {
        generatedId = keyResult.result().getKey();
        IProperty field = getMapper().getIdInfo().getField();
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
