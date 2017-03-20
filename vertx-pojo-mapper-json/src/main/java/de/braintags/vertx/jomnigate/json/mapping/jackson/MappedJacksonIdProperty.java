/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.mapping.jackson;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedIdField;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;
import de.braintags.vertx.jomnigate.mapping.IProperty;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MappedJacksonIdProperty extends IndexedIdField implements IMappedIdField {

  private IProperty idProperty;

  /**
   * Generate a mapped ID field with the name of the mapped field as field- and column name
   * 
   * @param idProperty
   *          the idProperty that has the {@link Id} annotation
   */
  public MappedJacksonIdProperty(IProperty idProperty) {
    super(idProperty.getName());
    this.idProperty = idProperty;
  }

  /**
   * Generate a mapped ID field with the name of the mapped field as field name, but a custom column name
   * 
   * @param idProperty
   *          the idProperty that has the {@link Id} annotation
   * @param columnName
   *          the name of the ID column
   */
  public MappedJacksonIdProperty(IProperty idProperty, String columnName) {
    super(idProperty.getName(), columnName);
    this.idProperty = idProperty;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMappedIdField#getField()
   */
  @Override
  public IProperty getField() {
    return idProperty;
  }

}
