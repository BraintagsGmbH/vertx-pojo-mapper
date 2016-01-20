/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.annotation.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The value of this annotation defines the execution of a function, which must exist inside the datastore. The function
 * will be executed, when an instance is saved into the datastore.
 * For all fields, which are annotated by Function, not the value of the field will be saved, but the function will be
 * transferred, so that it is executed in the datastore.
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Function {

  /**
   * Specify the function call, which shall be executed
   * 
   * @return the function call to be executed
   */
  String value() default "";

  /**
   * Defines the state of the field, when the function shall be executed
   * Default is {@link FieldState#ALL}
   * 
   * @return the state of the field
   */
  FieldState fieldState() default FieldState.ALL;
}
