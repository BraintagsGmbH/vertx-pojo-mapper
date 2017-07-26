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
package de.braintags.vertx.jomnigate.mapping;

/**
 * Possible options an index definition can set
 * 
 * @author sschmitt
 *
 */
public class IndexOption {

  private final IndexFeature feature;
  private final Object value;

  /**
   * Create a new setting for an index
   * 
   * @param feature
   *          the feature to set
   * @param value
   *          the value to set the feature to
   */
  public IndexOption(final IndexFeature feature, final Object value) {
    this.feature = feature;
    this.value = value;
  }

  /**
   * The feature this option defines
   * 
   * @return the feature
   */
  public IndexFeature getFeature() {
    return feature;
  }

  /**
   * The value this option sets the feature to
   * 
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "IndexOption [feature=" + feature + ", value=" + value + "]";
  }

  /**
   * Holds the different features that can be enabled or disabled
   */
  public static enum IndexFeature {
    /**
     * Defines that the values of this index must be unique
     */
    UNIQUE,
    /**
     * Defines a filter expression. Only elements matching the filter will be added to the index, and be constrained by
     * it if the "unique" flag is set
     */
    PARTIAL_FILTER_EXPRESSION;
  }
}
