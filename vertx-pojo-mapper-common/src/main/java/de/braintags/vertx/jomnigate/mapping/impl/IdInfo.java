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

import de.braintags.vertx.jomnigate.dataaccess.query.IdField;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedIdField;
import de.braintags.vertx.jomnigate.mapping.IIdInfo;
import de.braintags.vertx.jomnigate.mapping.IProperty;

/**
 * Default implementation of {@link IIdInfo}
 * 
 * @author sschmitt
 *
 */
public class IdInfo implements IIdInfo {

  private final IProperty field;
  private final IdField idField;

  /**
   * Create a new instance with info from a property
   * 
   * @param field
   *          the property to create the ID info from
   */
  public IdInfo(final IProperty field) {
    this.field = field;
    this.idField = new IndexedIdField(field.getName());
  }

  /* (non-Javadoc)
   * @see de.braintags.vertx.jomnigate.mapping.impl.IIdInfo#getIndexedField()
   */
  @Override
  public IdField getIndexedField() {
    return idField;
  }

  /* (non-Javadoc)
   * @see de.braintags.vertx.jomnigate.mapping.impl.IIdInfo#getField()
   */
  @Override
  public IProperty getField() {
    return field;
  }
}
