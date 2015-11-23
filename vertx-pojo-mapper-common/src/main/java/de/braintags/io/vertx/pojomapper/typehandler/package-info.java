/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * When objects shall be stored into or read from a datastore, the values must be converted in many
 * cases. This is the job of an {@link de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler}. During the mapping of
 * a mapper property the suitable ITypeHandler is detected by requesting the
 * {@link de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory} of the underlaying
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}. The found ITypeHandler is stored inside the appropriate
 * {@link de.braintags.io.vertx.pojomapper.mapping.IField} and from there used, when a value is read from or shall be
 * written into the datastore.
 * 
 * 
 * @author Michael Remme
 * 
 */
package de.braintags.io.vertx.pojomapper.typehandler;