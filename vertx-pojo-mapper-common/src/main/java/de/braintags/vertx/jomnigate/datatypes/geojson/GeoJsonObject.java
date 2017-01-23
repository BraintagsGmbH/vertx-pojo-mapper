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
package de.braintags.vertx.jomnigate.datatypes.geojson;

/**
 * An abstract class for representations of GeoJSON geographic objects.
 * 
 * @author Michael Remme
 * 
 */
public abstract class GeoJsonObject {

  /**
   * Get the {@link GeoJsonType} which is covered by the imeplementation
   * 
   * @return
   */
  public abstract GeoJsonType getType();

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
