/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * In contrast to a programmatical initialization of an IDataStore, you may initialize it by using
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
 * This method uses the {@link de.braintags.io.vertx.pojomapper.init.DataStoreSettings#getProperties()} to create a
 * suitable IDataStore.
 * The properties, which can be set here, are depending from the implementation of IDataStoreInit, please refer to the
 * extensions for MySql and MongoDb, for instance.
 * 
 * [source, java]
 * ----
 * public DataStoreSettings loadDataStoreSettings(String path) {
 * FileSystem fs = vertx.fileSystem();
 * if (fs.existsBlocking(path)) {
 * Buffer buffer = fs.readFileBlocking(path);
 * DataStoreSettings settings = Json.decodeValue(buffer.toString(), DataStoreSettings.class);
 * return settings;
 * } else {
 * IDataStoreSettings settings = MongoDataStoreInit.createDefaultSettings();
 * fs.writeFileBlocking(path, Buffer.buffer(Json.encode(settings)));
 * throw new FileSystemException("File did not exist and was created new in path " + path);
 * }
 * }
 * 
 * ----
 * The above method loads the DataStoreSettings from the filesystem as Json format. If the file doesn't exist, the
 * default settings are created by requesting a static method of MongoDataStoreInit. After they are saved at the
 * expected location and an exeption is thrown, to force the user to edit them.
 * 
 */
package de.braintags.io.vertx.pojomapper.init;
