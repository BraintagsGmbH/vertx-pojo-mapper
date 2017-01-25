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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.util.IteratorAsync;
import io.vertx.docgen.Source;

@Source(translate = false)
public class Examples {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(Examples.class);

  public void executeNative(IDataStore datastore) {
    IQuery<ExampleMapper> query = datastore.createQuery(ExampleMapper.class);
    String qs = "select * from ExampleMapper where name LIKE \"native%\"";
    query.setNativeCommand(qs);
    query.execute(qr -> {
      if (qr.succeeded()) {
        IteratorAsync<ExampleMapper> it = qr.result().iterator();
        while (it.hasNext()) {

        }
      }
    });
  }

  @Entity
  private static class ExampleMapper {
    @Id
    public String id = null;
    public String name = "testName";
  }

}