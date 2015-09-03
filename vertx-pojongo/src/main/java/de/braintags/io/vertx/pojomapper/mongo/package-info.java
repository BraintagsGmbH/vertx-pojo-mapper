/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * == Implementation of pojo mapper for Mongo
 *
 * We provide an implementation of {@link de.braintags.io.vertx.pojomapper.IDataStore} which uses the Vert.x {@link io.vertx.ext.mongo.MongoClient}
 * to perform mapping of POJOs into the datastore
 *
 * To create an instance you first need an instance of {@link io.vertx.ext.mongo.MongoClient}. To learn how to create one
 * of those please consult the documentation for the MongoClient.
 *
 * Once you've got one of those you can create a {@link io.vertx.ext.auth.mongo.MongoAuth} instance as follows:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example1(io.vertx.core.Vertx )}
 * ----
 * 
 * 
 * @author Michael Remme
 * 
 */

@Document(fileName = "index.adoc")
@GenModule(name = "vertx-pojongo", groupPackageName = "de.braintags")
package de.braintags.io.vertx.pojomapper.mongo;

import io.vertx.codegen.annotations.GenModule;
import io.vertx.docgen.Document;

