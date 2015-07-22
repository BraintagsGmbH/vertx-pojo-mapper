/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.ObjectReference;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.pojomapper.typehandler.impl.DefaultTypeHandlerResult;

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
  public void fromStore(Object source, IField field, Class<?> cls, ITypeHandlerResult typeHandlerResult) {
    typeHandlerResult.setResult(null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void intoStore(Object source, IField field, ITypeHandlerResult typeHandlerResult) {
    Object obToReference = ((ObjectReference) source).getReference();
    IMapperFactory mf = field.getMapper().getMapperFactory();
    IMapper subMapper = mf.getMapper(obToReference.getClass());
    IDataStore store = mf.getDataStore();
    IWrite<Object> write = (IWrite<Object>) store.createWrite(obToReference.getClass());
    write.add(obToReference);

    write.save(result -> {
      if (result.failed()) {
        typeHandlerResult.setException(result.cause());
      } else {
        IField idField = subMapper.getIdField();
        Object id = result.result().getId();
        if (id == null)
          id = idField.getPropertyAccessor().readData(obToReference);
        if (id == null) {
          throw new MappingException(String.format("Error after saving instancde: @Id field of mapper %s is null.",
              obToReference.getClass().getName()));
        }
        ITypeHandler th = mf.getDataStore().getTypeHandlerFactory().getTypeHandler(id.getClass());
        DefaultTypeHandlerResult tmpResult = new DefaultTypeHandlerResult();
        th.intoStore(id, field, tmpResult);
        tmpResult.validate();
        typeHandlerResult.setResult(tmpResult.getResult());
      }

    });
  }

}
