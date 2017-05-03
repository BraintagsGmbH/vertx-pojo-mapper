/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.converter;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.testdatastore.mapper.versioning.VersioningWithInterface_V7;
import de.braintags.vertx.jomnigate.versioning.IVersionConverter;
import io.vertx.core.Future;

public class V7Converter implements IVersionConverter<VersioningWithInterface_V7> {
  public static boolean executed;

  @Override
  public Future<Void> convert(IDataStore<?, ?> datastore, VersioningWithInterface_V7 toBeConverted,
      IObserverContext context) {
    toBeConverted.newName = "converted Value V7";
    executed = true;
    return Future.succeededFuture();
  }

}
