/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.testdatastore.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@Entity
public class TriggerMapper {
  @Id
  public String id = null;
  public String name = "testName";
  public String beforeSaveWithDataStore = "testName";
  public String afterSave = "testName";
  public String afterSaveWithDataStore = "testName";

  public String afterLoad;
  public String afterLoadWithDatastore;

  public String afterDelete;
  public String afterDeleteWithDatastore;

  public String beforeDelete;
  public String beforeDeleteWithDatastore;

  public TriggerMapper() {
  }

  public TriggerMapper(String name) {
    this.name = name;
  }

  @AfterDelete
  public void afterDelete() {
    this.afterDelete = "afterDelete";
  }

  @AfterDelete
  public void afterDeleteWithDatastore(ITriggerContext th) {
    checkTriggerContext(th);
    this.afterDeleteWithDatastore = "afterDeleteWithDatastore";
    th.complete();
  }

  @BeforeDelete
  public void beforeDelete() {
    this.beforeDelete = "beforeDelete";
  }

  @BeforeDelete
  public void beforeDeleteWithDatastore(ITriggerContext th) {
    checkTriggerContext(th);
    this.beforeDeleteWithDatastore = "beforeDeleteWithDatastore";
    th.complete();
  }

  @BeforeSave
  public void beforeSave() {
    this.name = "beforeSave";
  }

  @BeforeSave
  public void beforeSaveWithParameter(ITriggerContext th) {
    checkTriggerContext(th);
    this.beforeSaveWithDataStore = "beforeSaveWithDataStore";
    th.complete();
  }

  @AfterSave
  public void afterSave() {
    this.afterSave = "afterSave";
  }

  @AfterSave
  public void afterSaveWithDataStore(ITriggerContext th) {
    checkTriggerContext(th);
    this.afterSaveWithDataStore = "afterSaveWithDataStore";
    th.complete();
  }

  @AfterLoad
  public void afterLoad() {
    this.afterLoad = "afterLoad";
  }

  @AfterLoad
  public void afterLoadWithDatastore(ITriggerContext th) {
    checkTriggerContext(th);
    this.afterLoadWithDatastore = "afterLoadWithDatastore";
    th.complete();
  }

  private void checkTriggerContext(ITriggerContext th) {
    if (th == null) {
      throw new NullPointerException("triggercontext is null");
    }
    if (th.getMapper() == null) {
      throw new NullPointerException("mapper is null");
    }
  }
}
