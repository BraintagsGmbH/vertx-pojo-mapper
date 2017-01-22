/*-
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

package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;

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
