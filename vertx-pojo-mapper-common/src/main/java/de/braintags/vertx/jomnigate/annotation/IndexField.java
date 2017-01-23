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
 * Defines one field of the index
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface IndexField {
  /**
   * The name of the field to be used for the index
   * 
   * @return
   */
  String fieldName();

  /**
   * The type of the index as {@link IndexType}
   * 
   * @see IndexType
   */
  IndexType type() default IndexType.ASC;

  /**
   * The weight to use when creating a text index. This value only makes sense when direction is {@link IndexType#TEXT}
   */
  int weight() default -1;
}
