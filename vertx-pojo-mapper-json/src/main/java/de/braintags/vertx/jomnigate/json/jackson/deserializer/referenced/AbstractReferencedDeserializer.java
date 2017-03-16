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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.json.dataaccess.JsonStoreObject;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.AbstractDataStoreDeserializer;
import de.braintags.vertx.jomnigate.util.QueryHelper;
import io.vertx.core.Future;

/**
 * Abstract implementation of Deserializer for Referenced annotation
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractReferencedDeserializer<T> extends AbstractDataStoreDeserializer<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractReferencedDeserializer.class);
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  private SettableBeanProperty beanProperty;
  private ValueInstantiator valueInstantiator;

  /**
   * @param datastore
   * @param beanProperty
   */
  public AbstractReferencedDeserializer(IDataStore datastore, SettableBeanProperty beanProperty) {
    super(datastore, null);
    this.beanProperty = beanProperty;
  }

  /**
   * @return the beanProperty
   */
  public SettableBeanProperty getBeanProperty() {
    return beanProperty;
  }

  /**
   * Loads an instance from the datastore by the referenced ID
   * 
   * @param store
   *          the {@link IDataStore} to be used
   * @param subMapper
   * @param id
   * @param resultHandler
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future getReferencedObjectById(Object id, Class mapperClass) {
    LOGGER.debug("start getReferencedObjectById");
    Future<Object> f = Future.future();
    QueryHelper.findRecordById(getDatastore(), mapperClass, id.toString(), f.completer());
    return f;
  }

  /**
   * Loads all instances by id from the given array and stores them into the given collection
   * 
   * @param node
   * @param mapperClass
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future<Collection<?>> getReferencedObjectsById(ArrayNode node, Class mapperClass, Collection destination) {
    if (node == null || node.size() <= 0) {
      return null;
    }
    LOGGER.debug("start getReferencedObjectsById");
    IQuery q = getDatastore().createQuery(mapperClass);
    q.setSearchCondition(ISearchCondition.in(q.getMapper().getIdField(), createIdList(node)));
    Future<Collection<?>> f = Future.future();
    QueryHelper.executeToList(q, res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        destination.addAll(res.result());
        f.complete(destination);
      }
    });
    return f;
  }

  /**
   * Loads all instances by id from the given array and stores them into the given collection
   * 
   * @param node
   * @param mapperClass
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future getReferencedObjectsByIdAsArray(ArrayNode node, Class mapperClass) {
    if (node == null || node.size() < 0) {
      return null;
    }
    LOGGER.debug("start getReferencedObjectsById");
    Object resultArray = Array.newInstance(mapperClass, node.size());
    Future f = Future.future();
    if (node.size() > 0) {
      IQuery q = getDatastore().createQuery(mapperClass);
      q.setSearchCondition(ISearchCondition.in(q.getMapper().getIdField(), createIdList(node)));
      QueryHelper.executeToList(q, res -> {
        if (res.failed()) {
          f.fail(res.cause());
        } else {
          List qr = res.result();
          for (int i = 0; i < qr.size(); i++) {
            Array.set(resultArray, i, qr.get(i));
          }
          f.complete(resultArray);
        }
      });
    } else {
      f.complete(resultArray);
    }
    return f;
  }

  private List<?> createIdList(ArrayNode node) {
    List<Object> idList = new ArrayList<>();
    node.forEach(an -> idList.add(an.asText()));
    return idList;
  }

  /**
   * Creates am empty instance for the current deserializer
   * 
   * @param ct
   * @param type
   * @return
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  protected T instantiate(DeserializationContext ct, JavaType type) throws IOException {
    T result;
    ValueInstantiator vi = getValueInstantiator(ct, type);
    if (vi.canInstantiate()) {
      if (vi.canCreateUsingDefault()) {
        result = (T) vi.createUsingDefault(ct);
      } else {
        result = instantiateInternal(ct, type);
      }
    } else {
      result = instantiateInternal(ct, type);
    }
    return result;
  }

  /**
   * Called, if instantiate fails and used to create an empty instance internal
   * 
   * @param ct
   * @param type
   * @return
   */
  protected T instantiateInternal(DeserializationContext ct, JavaType type) {
    return null;
  }

  protected ValueInstantiator getValueInstantiator(DeserializationContext ct, JavaType type) throws IOException {
    if (valueInstantiator == null) {
      BeanDescription desc = ct.getConfig().introspectClassAnnotations(type);
      valueInstantiator = ct.getFactory().findValueInstantiator(ct, desc);
    }
    return valueInstantiator;
  }

  /**
   * Stores the pure Future into a PostHandler; JsonStoreObject will await finishing of the future
   * 
   * @param ct
   * @param f
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void storePostHandler(DeserializationContext ct, Future f) {
    List<ReferencedPostHandler> list = (List<ReferencedPostHandler>) ct
        .findInjectableValue(JsonStoreObject.REFERENCED_LIST, null, null);
    if (list == null) {
      throw new IllegalArgumentException("Referenced needs a List<ReferencedPreHandler> as injectable ");
    }
    list.add(new ReferencedPostHandler(f));
  }

  /**
   * Stores the Future and the data into a PostHandler. JsonDataStore will await finish of the Future and will store the
   * result into the given instance
   * 
   * @param ct
   * @param instance
   * @param f
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void storePostHandler(DeserializationContext ct, Object instance, Future f) {
    List<ReferencedPostHandler> list = (List<ReferencedPostHandler>) ct
        .findInjectableValue(JsonStoreObject.REFERENCED_LIST, null, null);
    if (list == null) {
      throw new IllegalArgumentException("Referenced needs a List<ReferencedPreHandler> as injectable ");
    }
    list.add(new ReferencedPostHandler(f, instance, getBeanProperty()));
  }

}
