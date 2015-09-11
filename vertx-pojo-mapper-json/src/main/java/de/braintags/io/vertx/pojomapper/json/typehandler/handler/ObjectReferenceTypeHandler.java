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
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.exception.PropertyAccessException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.ObjectReference;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;

/**
 * Handles instances of {@link ObjectReference}.
 * 
 * @author Michael Remme
 * 
 */

public class ObjectReferenceTypeHandler extends AbstractTypeHandler {

  /**
   * @param classesToDeal
   */
  public ObjectReferenceTypeHandler() {
    super(ObjectReference.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Class<?> mapperClass = (cls != null ? cls : field.getType());
    if (mapperClass == null) {
      fail(new NullPointerException("undefined mapper class"), resultHandler);
      return;
    }

    IMapperFactory mf = field.getMapper().getMapperFactory();
    IMapper subMapper = mf.getMapper(mapperClass);
    IDataStore store = mf.getDataStore();
    IQuery<?> query = (IQuery<?>) store.createQuery(mapperClass).field(subMapper.getIdField().getName()).is(source);
    query.execute(result -> {
      if (result.failed()) {
        fail(result.cause(), resultHandler);
      } else {
        if (result.result().size() != 1) {
          String formated = String.format("expected to find 1 record, but found %d in column %s with query '%s'",
              result.result().size(), subMapper.getTableInfo().getName(), result.result().getOriginalQuery());
          fail(new PropertyAccessException(formated), resultHandler);
          return;
        }
        result.result().iterator().next(iResult -> {
          if (iResult.failed()) {
            fail(iResult.cause(), resultHandler);
          } else
            success(iResult.result(), resultHandler);
          return;
        });
      }
      return;
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @SuppressWarnings({ "unchecked" })
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Object obToReference = ((ObjectReference) source).getReference();
    IMapperFactory mf = field.getMapper().getMapperFactory();
    IMapper subMapper = mf.getMapper(obToReference.getClass());
    IDataStore store = mf.getDataStore();
    IWrite<Object> write = (IWrite<Object>) store.createWrite(obToReference.getClass());
    write.add(obToReference);

    write.save(result -> {
      if (result.failed()) {
        fail(result.cause(), resultHandler);
      } else {
        IWriteEntry we = result.result().iterator().next();
        IField idField = subMapper.getIdField();
        Object id = we.getId();
        if (id == null)
          id = idField.getPropertyAccessor().readData(obToReference);
        if (id == null) {
          fail(
              new MappingException(String.format("Error after saving instancde: @Id field of mapper %s is null.",
                  obToReference.getClass().getName())), resultHandler);
          return;
        }
        ITypeHandler th = mf.getDataStore().getTypeHandlerFactory().getTypeHandler(id.getClass());
        th.intoStore(id, field, tmpResult -> {
          if (tmpResult.failed()) {
            resultHandler.handle(tmpResult);
          } else {
            Object dest = tmpResult.result().getResult();
            success(dest, resultHandler);
          }

        });
      }

    });
  }

}
