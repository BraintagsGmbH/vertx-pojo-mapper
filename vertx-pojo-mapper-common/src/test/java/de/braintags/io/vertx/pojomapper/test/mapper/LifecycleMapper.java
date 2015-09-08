/*
 * Copyright 2015 Braintags GmbH
 * 
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

package de.braintags.io.vertx.pojomapper.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;

/**
 * 
 *
 * @author Michael Remme
 * 
 */
public class LifecycleMapper {
  @Id
  public String id;
  public String name;

  public String afterLoadProperty = null;
  public String beforeSaveProperty = null;
  public String afterSaveProperty = null;
  public String beforeDeleteProperty = null;
  public String afterDeleteProperty = null;

  public LifecycleMapper() {
  }

  @AfterLoad
  public void lcAfterLoad() {
    afterLoadProperty = "after load";
  }

  @BeforeSave
  public void lcBeforeSave() {
    beforeSaveProperty = "before save";
  }

  @AfterSave
  public void lcAfterSave() {
    afterSaveProperty = "after save";
  }

  @BeforeDelete
  public void beforeDelete() {
    beforeDeleteProperty = "before delete";
  }

  @AfterDelete
  public void afterDelete() {
    afterDeleteProperty = "after delete";
  }

}
