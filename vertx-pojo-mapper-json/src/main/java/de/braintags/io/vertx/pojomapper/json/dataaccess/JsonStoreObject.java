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
package de.braintags.io.vertx.pojomapper.json.dataaccess;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractStoreObject;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IStoreObject}, which uses a JsonObject as internal container
 * 
 * @author Michael Remme
 */

public class JsonStoreObject extends AbstractStoreObject<JsonObject> {

  /**
   * Constructor
   * 
   * @param mapper
   *          the {@link IMapper} to be used
   * @param entity
   *          the entity to be used
   */
  public JsonStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity, new JsonObject());
  }

  /**
   * Constructor
   * 
   * @param jsonObject
   *          the {@link JsonObject} read from the datastore
   * @param mapper
   *          the mapper to be used
   */
  public JsonStoreObject(JsonObject jsonObject, IMapper mapper) {
    super(jsonObject, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#get(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object get(IField field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().getValue(colName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject#hasProperty(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public boolean hasProperty(IField field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().containsKey(colName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#put(de.braintags.io.vertx.pojomapper.mapping.IField,
   * java.lang.Object)
   */
  @Override
  public IStoreObject<JsonObject> put(IField field, Object value) {
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      throw new MappingException("Can't find columninfo for field " + field.getFullName());
    }
    if (field.isIdField() && value != null) {
      setNewInstance(false);
    }
    if (value == null) {
      getContainer().putNull(ci.getName());
    } else {
      getContainer().put(ci.getName(), value);
    }
    return this;
  }

}
