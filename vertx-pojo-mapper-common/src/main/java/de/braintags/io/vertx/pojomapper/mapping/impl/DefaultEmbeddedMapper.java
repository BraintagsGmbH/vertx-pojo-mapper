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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultEmbeddedMapper extends AbstractSubobjectMapper implements IEmbeddedMapper {

  /**
   * 
   */
  public DefaultEmbeddedMapper() {
  }

  @Override
  public void writeMap(Map<?, ?> javaValue, IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void writeArray(Object[] javaValue, IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void writeCollection(Iterable<?> javaValue, IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void writeSingleValue(Object referencedObject, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Object>> handler) {
  }

  @Override
  public void readMap(IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void readArray(IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void readCollection(IStoreObject<?> storeObject, IField field) {
  }

  @Override
  public void readSingleValue(Object entity, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Object>> handler) {
  }

}
