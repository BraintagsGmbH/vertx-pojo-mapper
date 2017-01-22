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
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;
import io.vertx.ext.unit.TestContext;

/**
 * a mapper with a long running triggers beforeSave. Both must be finished
 * 
 * @author Michael Remme
 * 
 */

@Entity
public class MultipleTriggerMapper {
  private static final int LOOP = 20;
  @Id
  public String id = null;
  public String name;
  public String[] triggerSeries1 = new String[LOOP];
  public String[] triggerSeries2 = new String[LOOP];
  public String[] triggerSeries3 = new String[LOOP];
  public String[] triggerSeries4 = new String[LOOP];

  public MultipleTriggerMapper() {
  }

  public MultipleTriggerMapper(String name) {
    this.name = name;
  }

  @BeforeSave
  public void beforeSave() {
    this.name = "beforeSave";
  }

  @BeforeSave
  public void beforeSaveWithParameter1(ITriggerContext th) {
    checkTriggerContext(th);
    triggerSeries1 = createArray();
    th.complete();
  }

  @BeforeSave
  public void beforeSaveWithParameter2(ITriggerContext th) {
    checkTriggerContext(th);
    triggerSeries2 = createArray();
    th.complete();
  }

  @BeforeSave
  public void beforeSaveWithParameter3(ITriggerContext th) {
    checkTriggerContext(th);
    triggerSeries3 = createArray();
    th.complete();
  }

  @BeforeSave
  public void beforeSaveWithParameter4(ITriggerContext th) {
    checkTriggerContext(th);
    triggerSeries4 = createArray();
    th.complete();
  }

  private String[] createArray() {
    String[] res = new String[LOOP];
    for (int i = 0; i < LOOP; i++) {
      res[i] = String.valueOf(i);
    }
    return res;
  }

  public void validate(TestContext context) {
    context.assertEquals(name, "beforeSave");
    validateArray(context, "triggerSeries1", triggerSeries1);
    validateArray(context, "triggerSeries2", triggerSeries2);
    validateArray(context, "triggerSeries3", triggerSeries3);
    validateArray(context, "triggerSeries4", triggerSeries4);
  }

  private void validateArray(TestContext context, String arrayName, String[] arr) {
    context.assertNotNull(arr, "array is null: " + arrayName);
    for (int i = 0; i < arr.length; i++) {
      context.assertNotNull(arr[i], String.format("entry of array %s is null, number: %d", arrayName, i));
    }
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
