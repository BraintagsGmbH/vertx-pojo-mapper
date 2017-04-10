/*-
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
package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedIdField;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;

/**
 * Implementation of {@link IMappedIdField}
 * 
 * @author sschmitt
 * 
 */
public class MappedIdFieldImpl extends IndexedIdField implements IMappedIdField {

  private MappedField mappedField;

  /**
   * Generate a mapped ID field with the name of the mapped field as field- and column name
   * 
   * @param mappedField
   *          the mapped field that has the {@link Id} annotation
   */
  public MappedIdFieldImpl(MappedField mappedField) {
    super(mappedField.getName());
    this.mappedField = mappedField;
  }

  /**
   * Generate a mapped ID field with the name of the mapped field as field name, but a custom column name
   * 
   * @param mappedField
   *          the mapped field that has the {@link Id} annotation
   * @param columnName
   *          the name of the ID column
   */
  public MappedIdFieldImpl(MappedField mappedField, String columnName) {
    super(mappedField.getName(), columnName);
    this.mappedField = mappedField;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.MappedIdField#getField()
   */
  @Override
  public MappedField getField() {
    return mappedField;
  }

}
