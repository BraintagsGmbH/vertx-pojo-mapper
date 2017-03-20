/*-
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedField;

/**
 * Extension of the {@link PolyMapper} that should be saved in the same collection as the {@link PolyMapper}, but with
 * additional class information
 * 
 * @author sschmitt
 * 
 */
public class PolySubMapper extends PolyMapper {
  public static final IIndexedField SUBFIELD = new IndexedField("subField");

  private String subField;

  public String getSubField() {
    return subField;
  }

  public void setSubField(String subField) {
    this.subField = subField;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((subField == null) ? 0 : subField.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    PolySubMapper other = (PolySubMapper) obj;
    if (subField == null) {
      if (other.subField != null)
        return false;
    } else if (!subField.equals(other.subField))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PolySubMapper [subField=" + subField + ", getId()=" + getId() + ", getMainField()=" + getMainField() + "]";
  }

}
