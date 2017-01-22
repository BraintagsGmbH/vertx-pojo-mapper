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
 * The annotation defines a class to be a mappable instance and allows to give further information about the entity
 * inside the used datastore ( database table, column in mongo or others ). All classes, which shall be mapped into a
 * datastore must have this annotation
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Entity {
  public static final String UNDEFINED_NAME = "";

  /**
   * Defines the name of the entity inside the datastore. Default is an {@link #UNDEFINED_NAME}, which will lead to the
   * simple name of the class
   * 
   * @return
   */
  String name() default UNDEFINED_NAME;

  /**
   * Define some specific options, which can be interpreted by the implementation of the IDataStore
   * 
   * @return
   */
  EntityOption[] options() default {};
}
