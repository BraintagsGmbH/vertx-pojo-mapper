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
package de.braintags.io.vertx.pojomapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * By using this annotation the {@link IKeyGenerator} can be set for a certain {@link IMapper}. The name must refer to
 * an {@link IKeyGenerator} which is supported by the choosen {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface KeyGenerator {
  public static final String UNDEFINED_NAME = "";

  public static final String NULL_KEY_GENERATOR = "NULL";

  /**
   * Defines the name of the IKeyGenerator for an {@link IMapper}
   * 
   * @return
   */
  String value() default UNDEFINED_NAME;
}
