/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.exception.PropertyAccessException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * ObjectReference is used as carrier when reading objects from the datastore, which contain referenced objects
 * 
 * @author Michael Remme
 * 
 */
public class ObjectReference implements IObjectReference {
  private IField field;
  private Object id;
  private Class<?> mapperClass;

  /**
   * Create a new instance
   * 
   * @param field
   *          the underlaying field, where the object shall be stored
   * @param id
   *          the id of the instance
   * @param mapperClass
   *          the mapper class
   */
  public ObjectReference(IField field, Object id, Class<?> mapperClass) {
    this.field = field;
    this.id = id;
    this.mapperClass = mapperClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.IObjectReference#getField()
   */
  @Override
  public IField getField() {
    return field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.IObjectReference#resolveObject(de.braintags.io.vertx.pojomapper.
   * IDataStore, io.vertx.core.Handler)
   */
  @Override
  public void resolveObject(IDataStore store, Object instance, Handler<AsyncResult<Void>> resultHandler) {
    IMapper mapper = store.getMapperFactory().getMapper(mapperClass);
    IQuery<?> query = (IQuery<?>) store.createQuery(mapperClass).field(mapper.getIdField().getName()).is(id);
    query.execute(result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
        return;
      }
      if (result.result().size() != 1) {
        String formated = String.format("expected to find 1 record, but found %d in column %s with query '%s'",
            result.result().size(), mapper.getTableInfo().getName(), result.result().getOriginalQuery());
        resultHandler.handle(Future.failedFuture(new PropertyAccessException(formated)));
        return;
      }
      result.result().iterator()
          .next(iResult -> storeReferencedObject(iResult, store, mapper, instance, resultHandler));
    });
  }

  private void storeReferencedObject(AsyncResult<?> iResult, IDataStore store, IMapper mapper, Object instance,
      Handler<AsyncResult<Void>> resultHandler) {
    if (iResult.failed()) {
      resultHandler.handle(Future.failedFuture(iResult.cause()));
      return;
    }
    Object javaValue = iResult.result();
    try {
      IPropertyAccessor pAcc = field.getPropertyAccessor();
      pAcc.writeData(mapper, javaValue);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }
}
