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
package de.braintags.vertx.jomnigate.annotation.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;

/**
 * Specifies that a complex java object as subobject from a mapper shall be stored embedded inside the
 * {@link IDataStore}, which means it will be stored inside the same colection / table than the parent mapper as
 * field(s).
 * By using this annotation, you are forcing the system that for each subobject an ID is generated locally. Because of
 * that an embedded instance must be an {@link Entity} as well and the system must have an {@link IKeyGenerator}
 * running.
 * 
 * @author Michael Remme
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Embedded {

}
