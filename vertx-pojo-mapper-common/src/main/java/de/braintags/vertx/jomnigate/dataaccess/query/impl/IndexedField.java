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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;

/**
 * Default implementation of {@link IIndexedField}
 * 
 * @author sschmitt
 * 
 */
public class IndexedField implements IIndexedField {

  private final String fieldName;

  public IndexedField(final String name) {
    this.fieldName = name;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return fieldName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof IIndexedField))
      return false;
    IIndexedField other = (IIndexedField) obj;
    if (getFieldName() == null) {
      if (other.getFieldName() != null)
        return false;
    } else if (!getFieldName().equals(other.getFieldName()))
      return false;
    return true;
  }
}
