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
package de.braintags.vertx.jomnigate.observer.impl;

import java.util.UUID;

import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

/**
 * This observer serializes records as json file into a defined directory. It does not return a Future, so that
 * the caller must not wait for the result. Occuring errors are logged. Per record one file is created.
 * 
 * @author Michael Remme
 * 
 */
public class JsonSerializationObserver extends AbstractObserver {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JsonSerializationObserver.class);

  /**
   * The name of the property, which defines the path of the directory, where the output shall be stored
   */
  public static final String DIRECTORY_PROPERTY = "directoryPath";

  private String parentDir;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    try {
      Object source = event.getSource();
      if (source != null) {
        Buffer encoded = Buffer.buffer(Json.encode(source));
        String path = createFileName(event);
        Vertx vertx = event.getDataStore().getVertx();
        vertx.fileSystem().writeFile(path, encoded, res -> {
          if (res.failed()) {
            checkCreateDirAndRetry(vertx, encoded, path, res.cause());
          } else {
            // we did it
          }
        });
      }
    } catch (Exception e) {
      LOGGER.error("error on serializing", e);
    }
    return null;
  }

  private void checkCreateDirAndRetry(Vertx vertx, Buffer encoded, String path, Throwable exception) {
    vertx.fileSystem().exists(parentDir, res -> {
      if (res.failed()) {
        LOGGER.error("directory check failed after exception in file creation", res.cause(), exception);
      } else {
        if (res.result()) {
          // the directory exists, so we can't solve the problem here
          LOGGER.error("error in JsonSerializationObserver, can't create output file", res.cause());
        } else {
          // create the directory
          vertx.fileSystem().mkdirs(parentDir, mkdirsResult -> {
            if (mkdirsResult.failed()) {
              LOGGER.error("error in JsonSerializationObserver, can't create parent directory", mkdirsResult.cause());
            } else {
              // parent directory created, write again
              vertx.fileSystem().writeFile(path, encoded, writeResult2 -> {
                if (writeResult2.failed()) {
                  // final error - log it
                  LOGGER.error("error in JsonSerializationObserver, can't create output file", writeResult2.cause());
                } else {
                  // we did it
                }
              });
            }
          });
        }
      }
    });
  }

  private String createFileName(IObserverEvent event) {
    return parentDir + event.getSource().getClass().getName() + "_" + UUID.randomUUID().toString() + ".json";
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.impl.AbstractObserver#init(io.vertx.core.Vertx)
   */
  @Override
  public void init(Vertx vertx) {
    parentDir = getObserverProperties().getProperty(DIRECTORY_PROPERTY);
    if (parentDir == null) {
      throw new ParameterRequiredException("The property " + DIRECTORY_PROPERTY + " must be set");
    }
    if (!parentDir.endsWith("/")) {
      parentDir += "/";
    }
    // this is done only once per init of observer
    vertx.fileSystem().mkdirsBlocking(parentDir);
  }

}
