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
package examples.mapper.converter;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.versioning.IVersionConverter;
import examples.mapper.VersionedMapper_V2;
import io.vertx.core.Future;
import io.vertx.docgen.Source;

@Source(translate = false)
public class V2Converter implements IVersionConverter<VersionedMapper_V2> {

  @Override
  public Future<Void> convert(IDataStore<?, ?> datastore, VersionedMapper_V2 toBeConverted, IObserverContext context) {
    toBeConverted.newProperty = "oldValue";
    return Future.succeededFuture();
  }

}
