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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import de.braintags.vertx.util.assertion.Assert;

/**
 * A representation of a GeoJSON Position, the fundamental geographic object of GeoJSON
 * 
 * @author Michael Remme
 * 
 */
public class Position {
  private final List<Double> values;

  /**
   * Construct an instance.
   *
   * @param values
   *          the non-null values
   */
  public Position(final List<Double> values) {
    Objects.requireNonNull(values, "values must not be null");
    Assert.isTrueArgument("value contains only non-null elements", !values.contains(null));
    Assert.isTrueArgument("value must contain at least two elements", values.size() >= 2);
    this.values = Collections.unmodifiableList(values);
  }

  /**
   * Construct an instance.
   *
   * @param values
   *          the non-null values
   */
  public Position(final Iterator<?> values) {
    Objects.requireNonNull(values, "values must not be null");
    List<Double> tl = new ArrayList<>();
    values.forEachRemaining(r -> tl.add(parseDouble(r)));
    Assert.isTrueArgument("value contains only non-null elements", !tl.contains(null));
    Assert.isTrueArgument("value must contain at least two elements", tl.size() >= 2);
    this.values = Collections.unmodifiableList(tl);
  }

  private Double parseDouble(Object o) {
    return o instanceof Double ? (Double) o : Double.parseDouble(String.valueOf(o));
  }

  /**
   * Construct an instance.
   *
   * @param first
   *          the first value
   * @param second
   *          the second value
   * @param remaining
   *          the remaining values
   */
  public Position(final double first, final double second, final double... remaining) {
    List<Double> tmp = new ArrayList<>();
    tmp.add(first);
    tmp.add(second);
    for (double cur : remaining) {
      tmp.add(cur);
    }
    this.values = Collections.unmodifiableList(tmp);
  }

  /**
   * Gets the values of this position
   * 
   * @return the values of the position
   */
  public List<Double> getValues() {
    return values;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Position that = (Position) o;

    if (!values.equals(that.values)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public String toString() {
    return "Position{" + "values=" + values + '}';
  }
}
