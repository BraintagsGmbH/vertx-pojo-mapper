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
package examples.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Observer;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import examples.DemoObserver;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
@Observer(observerClass = DemoObserver.class, priority = 400, eventTypes = { ObserverEventType.AFTER_DELETE,
    ObserverEventType.AFTER_LOAD })
public class AnnotatedObserver {
  @Id
  public String id;
  private String name;
  public int number;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
