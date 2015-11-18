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
package de.braintags.io.vertx.pojomapper.mapping.impl.keygen;

import java.io.IOException;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

/**
 * This generator generates keys and stores the current counter inside a local file
 * 
 * @author Michael Remme
 * 
 */
public class FileKeyGenerator extends AbstractKeyGenerator {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(FileKeyGenerator.class);
  public static final String NAME = "FileKeyGenerator";

  private static final String FILENAME = "pojomapperKeys";
  private JsonObject keyMap;
  private String fileDestination;

  /**
   * @param name
   * @param datastore
   */
  public FileKeyGenerator(IDataStore datastore) {
    super(NAME, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator#generateKey()
   */
  @Override
  public void generateKey(IMapper mapper, Handler<AsyncResult<Key>> handler) {
    String keyName = mapper.getMapperClass().getName();
    FileSystem fs = getDataStore().getVertx().fileSystem();
    if (keyMap == null) { // initially blocking to read the properties
      try {
        loadKeyMap(fs);
      } catch (IOException e) {
        handler.handle(Future.failedFuture(e));
        return;
      }
    }
    getKey(keyName, handler);
    storeKeyMap(fs);
  }

  private void getKey(String keyName, Handler<AsyncResult<Key>> handler) {
    long key = getNextKey(keyName);
    handler.handle(Future.succeededFuture(new Key(key)));
  }

  private long getNextKey(String keyName) {
    long key = keyMap.getLong(keyName, (long) 0);
    keyMap.put(keyName, ++key);
    return key;
  }

  /**
   * Not blocking, but no one is waiting for
   * 
   * @param fs
   */
  private void storeKeyMap(FileSystem fs) {
    fs.writeFile(fileDestination, Buffer.buffer(keyMap.encode()), result -> {
      if (result.failed()) {
        LOGGER.error("Error on saving file", result.cause());
      }
    });
  }

  /**
   * BLOCKING
   * 
   * @param fs
   * @throws IOException
   */
  private void loadKeyMap(FileSystem fs) throws IOException {
    String file = getDestinationDir();
    if (!fs.existsBlocking(file)) {
      fs.mkdirsBlocking(file);
    }
    fileDestination = getDestinationFile();
    if (fs.existsBlocking(fileDestination)) {
      Buffer buffer = fs.readFileBlocking(fileDestination);
      keyMap = new JsonObject(buffer.toString());
    } else {
      keyMap = new JsonObject();
    }
  }

  private static String getDestinationFile() {
    return getDestinationDir() + FILENAME;
  }

  private static String getDestinationDir() {
    return System.getProperty("user.home") + "/.pojomapper/";
  }
}
