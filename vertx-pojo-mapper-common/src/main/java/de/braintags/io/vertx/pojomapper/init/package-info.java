/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * In contrast to a programmatical initialization of an IDataStore, you may initialize by using
 * {@link de.braintags.io.vertx.pojomapper.init.DataStoreSettings}, which you can simply store as
 * a local file.
 * 
 * [source, java]
 * ----
 * {@link examples.Examples#example7(io.vertx.core.Vertx, io.vertx.core.Handler)}
 * ----
 * In this example first the {@link de.braintags.io.vertx.pojomapper.init.DataStoreSettings} are loaded from a local
 * file. Afterwards the {@link de.braintags.io.vertx.pojomapper.init.IDataStoreInit} is instantiated and then the
 * method IDataStoreInit#initDataStore is called.
 * This method uses the {@link java.util.Properties}, which are defined inside the
 * {@link de.braintags.io.vertx.pojomapper.init.DataStoreSettings#getProperties()} to create a suitable IDataStore.
 * The possible properties to be set are defined inside the appropriate implementation of
 * {@link de.braintags.io.vertx.pojomapper.init.IDataStoreInit}, please refer to the documentation there.
 * 
 * 
 * [source, java]
 * ----
 * {@link examples.Examples#loadDataStoreSettings(io.vertx.core.Vertx, String)}
 * ----
 * This method loads the {@link de.braintags.io.vertx.pojomapper.init.DataStoreSettings} from a local file from a Json
 * format directly. If they are't existing, they are created and saved as needed. For the properties to be set, refer to
 * the appropriate implementation of IDataStore.
 * 
 * 
 * 
 */
package de.braintags.io.vertx.pojomapper.init;