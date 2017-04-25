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

import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.versioning.IMapperVersion;

/**
 * Defines all information for a mapper, which shall be versioned and where for version conversion shall be executed. If
 * this info is defined, the mapper versioning system expects the mapper to be an instance of {@link IMapperVersion}
 * 
 * @author Michael Remme
 * 
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface VersionInfo {

  /**
   * Defines the event type, during which defined converters shall be executed
   * 
   * @return
   */
  ObserverEventType eventType() default ObserverEventType.BEFORE_UPDATE;

  /**
   * Defines the version of the mapper for the mapper versioning system. If this value is defined with a value > 0, the
   * mapper versioning system expects the mapper to be an instance of {@link IMapperVersion}
   * 
   * @return the current version of the mapper
   */
  long version() default -1;

  /**
   * Defines the possible version converters, which shall be executed for the given entity.
   * 
   * @return
   */
  VersionConverterDefinition[] versionConverter() default {};

}
