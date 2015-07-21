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

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.ObjectReference;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

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
  public Object fromStore(Object source, IField field, Class<?> cls) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object intoStore(Object source, IField field) {
    ObjectReference ref = (ObjectReference) source;
    IMapperFactory mf = field.getMapper().getMapperFactory();
    IMapper subMapper = mf.getMapper(ref.getReference().getClass());
    IField idField = subMapper.getIdField();
    Object id = idField.getPropertyAccessor().readData(ref.getReference());
    if (id == null) {
      throw new MappingException(String.format(
          "@Id field of mapper %s is null. Save the child record before saving the parent", ref.getReference()
              .getClass().getName()));
    }
    ITypeHandler th = mf.getDataStore().getTypeHandlerFactory().getTypeHandler(id.getClass());
    return th.intoStore(id, field);
  }

}
