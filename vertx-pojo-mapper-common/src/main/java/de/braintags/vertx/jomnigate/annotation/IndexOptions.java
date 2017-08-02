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
package de.braintags.vertx.jomnigate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes the options of an {@link Index}
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface IndexOptions {

  /**
   * Creates the index as a unique value index; inserting duplicates values in this field will cause errors
   */
  boolean unique() default false;
  
  /**
   * Defines a filter expression. Only elements matching the filter will be added to the index, and be constrained by it
   * if the "unique" flag is set
   */
  String partialFilterExpression() default "";

  /**
   * Ignores null values
   */
  boolean sparse() default false;
}
