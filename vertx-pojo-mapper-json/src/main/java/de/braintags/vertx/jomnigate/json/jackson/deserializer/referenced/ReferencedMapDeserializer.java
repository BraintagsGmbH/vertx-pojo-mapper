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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.util.QueryHelper;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedMapDeserializer extends AbstractReferencedDeserializer<Map<?, ?>> {
  private static final String ERROR_RESULT = "length of rawMap = %d; length of resultMap = %d";

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;
  private MapType rawMapType;
  private Class mapperClass;

  /**
   * @param datastore
   * @param beanProperty
   */
  public ReferencedMapDeserializer(IDataStore datastore, SettableBeanProperty beanProperty) {
    super(datastore, beanProperty);
    MapType type = (MapType) getBeanProperty().getType();
    TypeFactory tf = ((JsonDatastore) getDatastore()).getJacksonMapper().getTypeFactory();
    Class keyClass = type.getKeyType().getRawClass();
    rawMapType = tf.constructMapType(HashMap.class, keyClass, String.class);
    mapperClass = type.getContentType().getRawClass();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
   * com.fasterxml.jackson.databind.DeserializationContext)
   */
  @SuppressWarnings({ "rawtypes", "unused" })
  @Override
  public Map<?, ?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    Map rawMap = ctxt.readValue(p, rawMapType);
    Map<?, ?> resultMap = null;
    if (rawMap != null) {
      resultMap = instantiate(ctxt, getBeanProperty().getType());
      Future f = getReferencedObjectsById(mapperClass, rawMap, resultMap);
      storePostHandler(ctxt, f);
    }
    return resultMap;
  }

  @Override
  protected Map instantiateInternal(DeserializationContext ct, JavaType type) {
    return new HashMap<>();
  }

  /**
   * Loads all instances by id from the given array and stores them into the given collection.
   * NOTE: IN-query does not gurantee the order to be the same as the arguments. Thus we are executing ONE in query and
   * add the instances in the order like inside the source map.
   * Alternative would be to execute single queries for each entry of the
   * 
   * @param node
   * @param mapperClass
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future<Void> getReferencedObjectsById(Class mapperClass, Map rawMap, Map<?, ?> resultMap) {
    if (rawMap == null) {
      return null;
    }
    IQuery q = getDatastore().createQuery(mapperClass);
    q.setSearchCondition(ISearchCondition.in(q.getMapper().getIdField(), createIdList(rawMap)));
    Future<Void> f = Future.future();
    QueryHelper.executeToList(q, res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        List recList = res.result();
        try {
          fillMap(q.getMapper().getIdField().getField(), recList, rawMap, resultMap);
          f.complete();
        } catch (Exception e) {
          f.fail(e);
        }
      }
    });
    return f;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void fillMap(IProperty idField, List recList, Map<?, String> rawMap, Map resultMap) {
    rawMap.entrySet().forEach(entry -> {
      resultMap.put(entry.getKey(), findInstance(idField, entry.getValue(), recList));
    });
    if (resultMap.size() != rawMap.size()) {
      throw new IllegalArgumentException(String.format(ERROR_RESULT, rawMap.size(), resultMap.size()));
    }
  }

  /**
   * Fetches the instance with the given id.
   * NOTE: do NOT remove found entries, in case values are stored more than one time
   * 
   * @param idField
   * @param id
   * @param queryResult
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Object findInstance(IProperty idField, Object id, List queryResult) {
    Object ret = queryResult.stream().filter(o -> idField.getPropertyAccessor().readData(o).equals(id)).findFirst()
        .orElse(null);
    return ret;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private List<?> createIdList(Map rawMap) {
    List<Object> idList = new ArrayList<>();
    rawMap.values().stream().forEach(an -> idList.add(an));
    return idList;
  }

}
