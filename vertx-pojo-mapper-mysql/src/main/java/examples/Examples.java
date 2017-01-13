/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package examples;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
import de.braintags.io.vertx.util.IteratorAsync;
import io.vertx.docgen.Source;

@Source(translate = false)
public class Examples {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(Examples.class);

  public void executeNative(IDataStore datastore) {
    IQuery<MiniMapper> query = datastore.createQuery(MiniMapper.class);
    String qs = "select * from MiniMapper where name LIKE \"native%\"";
    query.setNativeCommand(qs);
    query.execute(qr -> {
      if (qr.succeeded()) {
        IteratorAsync<MiniMapper> it = qr.result().iterator();
        while (it.hasNext()) {

        }
      }
    });
  }
}
