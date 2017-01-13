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
package de.braintags.io.vertx.pojomapper.annotation.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * Specifies that the content of the field shall be encoded. The name of the annotation references
 * {@link IDataStore#getEncoder(String)}
 * 
 * @author Michael Remme
 * 
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Encoder {

  /**
   * Defines the name of the encoder, which shall be used.
   * 
   * @return
   */
  String name();

}
