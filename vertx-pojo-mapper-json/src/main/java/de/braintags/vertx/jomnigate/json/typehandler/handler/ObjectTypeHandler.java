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
package de.braintags.vertx.jomnigate.json.typehandler.handler;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.impl.DefaultPropertyMapper;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Deals all fields, which are instanced of Object and which are NOT annotated as {@link Referenced} or {@link Embedded}
 *
 * @author Michael Remme
 *
 */
public class ObjectTypeHandler extends AbstractTypeHandler {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DefaultPropertyMapper.class);

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   *
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ObjectTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Object.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation == null;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {

    try {
      JavaType targetType;

      if (cls == null) {
        targetType = Json.mapper.getTypeFactory().constructType(field.getGenericType());
      } else {
        targetType = Json.mapper.getTypeFactory().constructType(cls);
      }

      if (!((source instanceof JsonArray) || (source instanceof JsonObject))) {
        source = Json.mapper.convertValue(source, targetType);
      } else {
        String encoded;
        if (source instanceof JsonArray) {
          encoded = ((JsonArray) source).encode();
        } else {
          encoded = ((JsonObject) source).encode();
        }
        source = Json.mapper.readValue(encoded, targetType);
      }

      success(source, resultHandler);
    } catch (Exception e) {
      fail(e, resultHandler);
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    try {
      if (source == null) {
        success(null, resultHandler);
        return;
      }
      JsonNode jsonNode = Json.mapper.valueToTree(source);
      if (jsonNode.isValueNode()) {
        if (jsonNode.isNumber()) {
          success(jsonNode.numberValue(), resultHandler);
        } else if (jsonNode.isTextual()) {
          success(jsonNode.textValue(), resultHandler);
        } else if (jsonNode.isBoolean()) {
          success(jsonNode.asBoolean(), resultHandler);
        } else if (jsonNode.isBinary()) {
          success(jsonNode.binaryValue(), resultHandler);
        }
      } else if (jsonNode.isArray()) {
        JsonArray result = new JsonArray(Json.mapper.writeValueAsString(jsonNode));
        success(result, resultHandler);
      } else {
        JsonObject result = new JsonObject(Json.mapper.writeValueAsString(jsonNode));
        success(result, resultHandler);
      }
    } catch (Exception e) {
      fail(e, resultHandler);
    }

  }

}
