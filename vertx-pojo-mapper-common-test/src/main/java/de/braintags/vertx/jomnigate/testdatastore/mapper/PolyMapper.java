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

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;

/**
 * Mapper to test polymorphism, together with {@link PolySubMapper}
 * 
 * @author sschmitt
 * 
 */
@Entity(name = "PolyMapper", polyClass = IPolyMapper.class)
public class PolyMapper implements IPolyMapper {
  @Id
  private String id;
  private String mainField;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.mapper.IPolyMapper#getId()
   */
  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.mapper.IPolyMapper#getMainField()
   */
  @Override
  public String getMainField() {
    return mainField;
  }

  public void setMainField(String mainField) {
    this.mainField = mainField;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((mainField == null) ? 0 : mainField.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PolyMapper other = (PolyMapper) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (mainField == null) {
      if (other.mainField != null)
        return false;
    } else if (!mainField.equals(other.mainField))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PolyMapper [id=" + id + ", mainField=" + mainField + "]";
  }

}
