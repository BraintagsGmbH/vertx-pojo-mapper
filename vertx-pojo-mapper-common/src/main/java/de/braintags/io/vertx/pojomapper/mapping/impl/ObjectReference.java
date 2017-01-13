/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;

/**
 * ObjectReference is used as carrier when reading objects from the datastore, which contain referenced objects
 * 
 * @author Michael Remme
 * 
 */
public class ObjectReference implements IObjectReference {
  private IField field;
  private Object dbSource;

  /**
   * Create a new instance
   * 
   * @param field
   *          the underlaying field, where the object shall be stored
   * @param dbSource
   *          the value from the datastore
   * @param mapperClass
   *          the mapper class
   */
  public ObjectReference(IField field, Object dbSource) {
    this.field = field;
    this.dbSource = dbSource;
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

  @Override
  public Object getDbSource() {
    return dbSource;
  }
}
