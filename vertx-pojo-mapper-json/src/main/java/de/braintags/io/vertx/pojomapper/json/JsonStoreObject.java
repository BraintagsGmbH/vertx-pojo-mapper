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

package de.braintags.io.vertx.pojomapper.json;

import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * An implementation of {@link IStoreObject}, which uses a JsonObject as internal container
 * 
 * @author Michael Remme
 */

public class JsonStoreObject implements IStoreObject<JsonObject> {
  private JsonObject jsonObject;

  /**
   * 
   */
  public JsonStoreObject() {
    this.jsonObject = new JsonObject();
  }

  /**
   * 
   */
  public JsonStoreObject(JsonObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#get(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object get(IField field) {
    return jsonObject.getValue(field.getMappedFieldName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#put(de.braintags.io.vertx.pojomapper.mapping.IField,
   * java.lang.Object)
   */
  @Override
  public IStoreObject<JsonObject> put(IField field, Object value) {
    jsonObject.put(field.getMappedFieldName(), value);
    return this;
  }

  @Override
  public JsonObject getContainer() {
    return jsonObject;
  }

}
