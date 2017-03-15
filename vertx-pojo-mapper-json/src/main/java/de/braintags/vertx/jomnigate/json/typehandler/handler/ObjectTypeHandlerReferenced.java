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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.impl.ObjectReference;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerReferenced;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import de.braintags.vertx.util.exception.PropertyAccessException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Deals all fields, which are instances of Object and which are annotated as {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */

public class ObjectTypeHandlerReferenced extends ObjectTypeHandler implements ITypeHandlerReferenced {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(ObjectTypeHandlerReferenced.class);

  /**
   * @param typeHandlerFactory
   */
  public ObjectTypeHandlerReferenced(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation != null && annotation instanceof Referenced;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object id, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (id == null) {
      success(null, resultHandler);
    } else if (field.getMapper().handleReferencedRecursive()) {
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      ObjectReference objectReference = new ObjectReference(field, id);
      resolveReferencedObject(store, objectReference, resultHandler);
    } else {
      ObjectReference objectReference = new ObjectReference(field, id);
      success(objectReference, resultHandler);
    }
  }

  @Override
  public void resolveReferencedObject(IDataStore store, IObjectReference reference,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    LOGGER.debug("start resolveReferencedObject");
    Class<?> mapperClass = reference.getField().getType();
    if (mapperClass == null) {
      fail(new NullPointerException("undefined mapper class"), resultHandler);
      return;
    }
    IMapperFactory mf = store.getMapperFactory();
    IMapper subMapper = mf.getMapper(mapperClass);
    getReferencedObjectById(store, subMapper, reference.getDbSource(), resultHandler);
  }

  /**
   * Loads an instance from teh datastore by the referenced ID
   * 
   * @param store
   *          the {@link IDataStore} to be used
   * @param subMapper
   * @param id
   * @param resultHandler
   */
  public void getReferencedObjectById(IDataStore store, IMapper<?> subMapper, Object id,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    LOGGER.debug("start getReferencedObjectById");
    IQuery<?> query = store.createQuery(subMapper.getMapperClass());
    query.setSearchCondition(ISearchCondition.isEqual(subMapper.getIdField().getName(), id));
    query.execute(result -> {
      if (result.failed()) {
        fail(result.cause(), resultHandler);
        return;
      }
      if (result.result().size() != 1) {
        String formated = String.format("expected to find 1 record, but found %d in column %s with query '%s'",
            result.result().size(), subMapper.getTableInfo().getName(), result.result().getOriginalQuery());
        fail(new PropertyAccessException(formated), resultHandler);
        return;
      }
      result.result().iterator().next(iResult -> {
        if (iResult.failed()) {
          fail(iResult.cause(), resultHandler);
        } else {
          LOGGER.debug("referenced object successfully loaded");
          success(iResult.result(), resultHandler);
        }
      });
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object referencedObject, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    if (referencedObject == null) {
      success(null, resultHandler);
    } else {
      saveReferencedObject(store, referencedObject, storeResult -> {
        if (storeResult.failed()) {
          fail(storeResult.cause(), resultHandler);
        }
        Object id = storeResult.result();
        storeId(store, field, id, resultHandler);
      });
    }
  }

  private void storeId(IDataStore store, IField field, Object id,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    ITypeHandler th = store.getMapperFactory().getTypeHandlerFactory().getTypeHandler(id.getClass(), null);
    th.intoStore(id, field, tmpResult -> {
      if (tmpResult.failed()) {
        resultHandler.handle(tmpResult);
      } else {
        Object dest = tmpResult.result().getResult();
        success(dest, resultHandler);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void saveReferencedObject(IDataStore store, Object referencedObject,
      Handler<AsyncResult<Object>> resultHandler) {
    IWrite<Object> write = (IWrite<Object>) store.createWrite(referencedObject.getClass());
    IMapper subMapper = write.getMapper();
    write.add(referencedObject);
    write.save(saveResult -> {
      if (saveResult.failed()) {
        resultHandler.handle(Future.failedFuture(saveResult.cause()));
      }
      IWriteEntry we = saveResult.result().iterator().next();
      IField idField = subMapper.getIdField();
      Object id = we.getId() == null ? idField.getPropertyAccessor().readData(referencedObject) : we.getId();
      if (id == null) {
        resultHandler.handle(Future.failedFuture(new MappingException(String.format(
            "Error after saving instancde: @Id field of mapper %s is null.", referencedObject.getClass().getName()))));
        return;
      }
      resultHandler.handle(Future.succeededFuture(id));
    });
  }

}
