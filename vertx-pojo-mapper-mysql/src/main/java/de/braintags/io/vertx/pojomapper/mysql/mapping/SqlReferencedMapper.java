/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.mapping;

import java.util.Map;

import de.braintags.io.vertx.pojomapper.json.mapping.JsonReferencedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * An extension to handle referenced objects for sql based datastores
 * 
 * @author Michael Remme
 * 
 */

public class SqlReferencedMapper extends JsonReferencedMapper {

  @Override
  protected void writeCollection(Iterable<?> iterable, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    super.writeCollection(iterable, storeObject, field, result -> modifyJsonArray(storeObject, field, result, handler));
  }

  @Override
  protected void writeArray(Object[] javaValues, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    super.writeArray(javaValues, storeObject, field, result -> modifyJsonArray(storeObject, field, result, handler));
  }

  @Override
  protected void writeMap(Map<?, ?> map, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    super.writeMap(map, storeObject, field, result -> modifyJsonArray(storeObject, field, result, handler));
  }

  @Override
  protected void readMap(Object entity, IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler) {
    String arrayString = (String) storeObject.get(field);
    JsonArray array = new JsonArray(arrayString);
    storeObject.put(field, array);
    super.readMap(entity, storeObject, field, handler);
  }

  @Override
  protected void readInternal(IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Object>> handler) {
    String arrayString = (String) storeObject.get(field);
    JsonArray array = new JsonArray(arrayString);
    storeObject.put(field, array);
    super.readInternal(storeObject, field, handler);
  }

  /**
   * Encodes a {@link JsonArray} in the field into its String representation
   * 
   * @param storeObject
   * @param field
   * @param result
   * @param handler
   */
  private void modifyJsonArray(IStoreObject<?> storeObject, IField field, AsyncResult<Void> result,
      Handler<AsyncResult<Void>> handler) {
    if (result.failed()) {
      handler.handle(result);
      return;
    }

    JsonArray array = (JsonArray) storeObject.get(field);
    String encoded = array.encode();
    storeObject.put(field, encoded);
    handler.handle(result);
  }

}
