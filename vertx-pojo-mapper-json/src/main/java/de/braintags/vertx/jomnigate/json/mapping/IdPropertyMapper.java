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
package de.braintags.vertx.jomnigate.json.mapping;

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.util.exception.PropertyAccessException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * used to handle the id field of a mapper.
 * 
 * @author Michael Remme
 * 
 */
public class IdPropertyMapper implements IPropertyMapper {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(IdPropertyMapper.class);

  private static IdTranslator charTrans = id -> {
    return String.valueOf(id);
  };

  private static IdTranslator numericTrans = id -> {
    return id == null ? null : id instanceof Long ? id : Long.parseLong((String) id);
  };

  private IdTranslator idTrans;
  private IKeyGenerator keyGen;

  /**
   * @param field
   *          the field to be mapped
   */
  public IdPropertyMapper(IField field) {
    if (isCharacterColumn(field)) {
      idTrans = charTrans;
    } else if (isNumericColumn(field)) {
      idTrans = numericTrans;
    } else
      throw new UnsupportedOperationException("id column is nor numeric nor character: " + field.getType());
    keyGen = field.getMapper().getKeyGenerator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#intoStoreObject(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IStoreObject, de.braintags.vertx.jomnigate.mapping.IField,
   * io.vertx.core.Handler)
   */
  @Override
  public <T> void intoStoreObject(T entity, IStoreObject<T, ?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    Object javaValue = field.getPropertyAccessor().readData(entity);
    storeObject.put(field, javaValue == null ? null : String.valueOf(javaValue));
    handler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#readForStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public <T> void readForStore(T entity, IField field, Handler<AsyncResult<Object>> handler) {
    Object javaValue = field.getPropertyAccessor().readData(entity);
    handler.handle(Future.succeededFuture(javaValue == null ? null : String.valueOf(javaValue)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#fromStoreObject(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IStoreObject, de.braintags.vertx.jomnigate.mapping.IField,
   * io.vertx.core.Handler)
   */
  @Override
  public <T> void fromStoreObject(T entity, IStoreObject<T, ?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("starting fromStoreObject for field " + field.getFullName());
    Object javaValue = idTrans.translate(storeObject.get(field));
    try {
      IPropertyAccessor pAcc = field.getPropertyAccessor();
      pAcc.writeData(entity, javaValue);
      LOGGER.debug("writing data");
      handler.handle(Future.succeededFuture());
    } catch (PropertyAccessException e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  protected boolean isCharacterColumn(IField field) {
    return CharSequence.class.isAssignableFrom(field.getType());
  }

  protected boolean isNumericColumn(IField field) {
    return Number.class.isAssignableFrom(field.getType()) || field.getType().equals(long.class)
        || field.getType().equals(int.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyMapper#fromObjectReference(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IObjectReference, io.vertx.core.Handler)
   */
  @Override
  public void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler) {
  }

  @FunctionalInterface
  interface IdTranslator {
    Object translate(Object source);
  }

}
