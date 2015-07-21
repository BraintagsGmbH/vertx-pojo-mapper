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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.Map;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IReferencedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultReferencedMapper implements IReferencedMapper {

  /**
   * 
   */
  public DefaultReferencedMapper() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper#intoStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject, de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void intoStoreObject(Object entity, IStoreObject<?> storeObject, IField field) {
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(entity);
    if (javaValue == null)
      return;
    if (field.isMap()) {
      writeMap((Map<?, ?>) javaValue, storeObject, field);
    } else if (!field.isArray()) {
      writeArray((Object[]) javaValue, storeObject, field);
    } else if (!field.isSingleValue()) {
      writeCollection((Iterable<?>) javaValue, storeObject, field);
    } else {
      writeSingleValue(javaValue, storeObject, field);
    }
  }

  private void writeMap(Map<?, ?> javaValue, IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  private void writeArray(Object[] javaValue, IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  private void writeCollection(Iterable<?> javaValue, IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

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
  private void writeSingleValue(final Object referencedObject, final IStoreObject<?> storeObject, final IField field) {
    IMapperFactory mf = field.getMapper().getMapperFactory();
    IMapper subMapper = mf.getMapper(referencedObject.getClass());
    IField idField = subMapper.getIdField();
    Object id = idField.getPropertyAccessor().readData(referencedObject);
    if (id == null) {
      throw new MappingException(String.format(
          "@Id field of mapper %s is null. Save the child record before saving the parent", referencedObject.getClass()
              .getName()));
    }
    ObjectReference ref = new ObjectReference(id);
    ITypeHandler handler = mf.getDataStore().getTypeHandlerFactory().getTypeHandler(ref.getClass());
    handler.intoStore(ref, field);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper#fromStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject, de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void fromStoreObject(Object entity, IStoreObject<?> storeObject, IField field) {
    if (field.isMap()) {
      readMap(storeObject, field);
    } else if (!field.isArray()) {
      readArray(storeObject, field);
    } else if (!field.isSingleValue()) {
      readCollection(storeObject, field);
    } else {
      readSingleValue(storeObject, field);
    }
  }

  private void readMap(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  private void readArray(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  private void readCollection(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  private void readSingleValue(final IStoreObject<?> storeObject, final IField field) {
    throw new UnsupportedOperationException();
  }
}
