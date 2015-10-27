/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.mapping;

import de.braintags.io.vertx.pojomapper.exception.TypeHandlerException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.impl.ObjectReference;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Implementation of {@link IReferencedMapper} to deal with subobjects, which shall be stored by their id in the
 * datastore
 * 
 * @author Michael Remme
 * 
 */

public class JsonReferencedMapper extends AbstractSubobjectMapper implements IReferencedMapper {

  /**
   * Write the reference to a child record into the field. This method expects, that the child is saved before, so that
   * it got an id already
   * 
   * @param referencedObject
   *          the child instance to be handled
   * @param storeObject
   *          the {@link IStoreObject}
   * @param field
   *          the field, where the reference is stored inside
   */
  @Override
  public void writeSingleValue(final Object referencedObject, final IStoreObject<?> storeObject, final IField field,
      Handler<AsyncResult<Object>> handler) {
    ObjectReference ref = new ObjectReference(referencedObject);
    IMapperFactory mf = field.getMapper().getMapperFactory();
    ITypeHandler th = mf.getDataStore().getTypeHandlerFactory().getTypeHandler(ref.getClass());
    th.intoStore(ref, field, result -> {
      if (result.failed()) {
        Future<Object> future = Future.failedFuture(result.cause());
        handler.handle(future);
        return;
      } else {
        handler.handle(Future.succeededFuture(result.result().getResult()));
      }

    });
  }

  @Override
  public void readSingleValue(Object dbValue, final IField field, Class<?> mapperClass,
      Handler<AsyncResult<Object>> handler) {
    ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
        .getTypeHandler(ObjectReference.class);
    th.fromStore(dbValue, field, mapperClass, result -> {
      if (result.failed()) {
        Future<Object> future = Future.failedFuture(result.cause());
        handler.handle(future);
        return;
      } else {
        Object javaValue = result.result().getResult();
        if (javaValue == null && dbValue != null) {
          Future<Object> future = Future.failedFuture(new TypeHandlerException(
              String.format("Value conversion failed: original = %s, conversion = NULL", String.valueOf(dbValue))));
          handler.handle(future);
          return;
        }
        Future<Object> future = Future.succeededFuture(javaValue);
        handler.handle(future);
        return;
      }
    });

  }
}
