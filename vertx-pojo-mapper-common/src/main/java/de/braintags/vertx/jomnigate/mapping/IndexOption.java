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

  /**
   * Holds the different features that can be enabled or disabled
   */
  public static enum IndexFeature {
    /**
     * Defines that the values of this index must be unique
     */
    UNIQUE;
  }
}
