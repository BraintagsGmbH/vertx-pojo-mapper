/*-
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
package examples.mapper;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class LifecycleMapper {
  @Id
  public String id;
  public String name;

  public LifecycleMapper() {
  }

  @BeforeLoad
  public void beforeLoad() {
    name = "just before load";
  }

  @AfterLoad
  public void afterLoad(ITriggerContext triggerContext) {
    name = "just after load";
    IDataStore ds = triggerContext.getMapper().getMapperFactory().getDataStore();
    IQuery<MiniMapper> q = ds.createQuery(MiniMapper.class);
    q.setSearchCondition(q.isEqual("name", "test"));
    q.execute(qr -> {
      if (qr.failed()) {
        triggerContext.fail(qr.cause());
      } else {
        // do something
        triggerContext.complete();
      }
    });
  }

  @BeforeSave
  public void beforeSave() {
    name = "just before save";
  }

  @AfterSave
  public void afterSave() {
    name = "just after save";
  }

  @BeforeDelete
  public void beforeDelete() {
    name = "just before deletion";
  }

  @AfterDelete
  public void afterDelete() {
    name = "just after deletion";
  }

}
