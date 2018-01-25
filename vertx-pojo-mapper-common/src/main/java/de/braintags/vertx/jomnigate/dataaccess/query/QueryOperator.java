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
package de.braintags.vertx.jomnigate.dataaccess.query;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines the operators for a query
 *
 * @author Michael Remme
 *
 */
public enum QueryOperator {

  EQUALS(false, "="),
  EQUALS_IGNORE_CASE(false),
  NOT_EQUALS(false, "!="),

  LARGER(false, ">"),
  SMALLER(false, "<"),
  LARGER_EQUAL(false, ">="),
  SMALLER_EQUAL(false, "<="),

  STARTS(false, "starts with", "starts"),
  ENDS(false, "ends with", "ends"),
  CONTAINS(false, "contains"),

  NEAR(false, "near"),

  IN(true, "in"),
  NOT_IN(true, "not in");

  private static final Map<String, QueryOperator> translationMap = new HashMap<>();
  private final boolean multiValueOperator;
  private String[] synonyms;

  /**
   * Initialize the enum values
   *
   * @param multiValueOperator
   *          if the value of conditions with this operator must consist of multiple values, like IN
   * @param synonyms
   *          the synonyms of this value, with which text values can be translated to enum values
   */
  private QueryOperator(final boolean multiValueOperator, final String... synonyms) {
    this.multiValueOperator = multiValueOperator;
    this.synonyms = synonyms;
  }

  static {
    // add all synonyms to a static translation map
    EnumSet.allOf(QueryOperator.class).forEach(operator -> {
      if (operator.synonyms != null) {
        for (String synonym : operator.synonyms) {
          translationMap.put(synonym.toLowerCase(Locale.US), operator);
        }
      }
      translationMap.put(operator.name().toLowerCase(), operator);
      if (operator.name().contains("_"))
        translationMap.put(operator.name().replaceAll("_", "").toLowerCase(), operator);
    });
  }

  /**
   * @return if the value of conditions with this operator must consist of multiple values
   */
  public boolean isMultiValueOperator() {
    return multiValueOperator;
  }

  /**
   * Translate a text value to its matching query operator, for example "=" to "EQUALS"
   *
   * @param value
   *          the text value to translate
   * @return
   */
  @JsonCreator
  public static QueryOperator translate(final String value) {
    return translationMap.get(StringUtils.lowerCase(value, Locale.US));
  }

}

// NEAR("$near"),
// NEAR_SPHERE("$nearSphere"),
// WITHIN("$within"),
// WITHIN_CIRCLE("$center"),
// WITHIN_CIRCLE_SPHERE("$centerSphere"),
// WITHIN_BOX("$box"),
// GEO_WITHIN("$geoWithin"),
// EXISTS("$exists"),
// TYPE("$type"),
// NOT("$not"),
// MOD("$mod"),
// SIZE("$size"),
// ALL("$all"),
// ELEMENT_MATCH("$elemMatch"),
// WHERE("$where")
