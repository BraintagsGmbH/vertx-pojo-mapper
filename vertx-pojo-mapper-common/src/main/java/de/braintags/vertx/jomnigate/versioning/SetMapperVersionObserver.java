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
package de.braintags.vertx.jomnigate.versioning;

import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import de.braintags.vertx.jomnigate.init.ObserverMapperSettings;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import io.vertx.core.Future;

/**
 * The observer is responsible to set the version value of all entities, where a {@link VersionInfo} is
 * defined. The observer is programmatically added to the observer system with the highest priority.
 * 
 * @author Michael Remme
 * 
 */
public class SetMapperVersionObserver extends AbstractObserver {

  public static ObserverDefinition<SetMapperVersionObserver> createObserverSettings() {
    ObserverDefinition<SetMapperVersionObserver> settings = new ObserverDefinition<>(SetMapperVersionObserver.class);
    settings.setPriority(Integer.MAX_VALUE);
    settings.getEventTypeList().add(ObserverEventType.BEFORE_INSERT);
    ObserverMapperSettings ms = new ObserverMapperSettings(IMapperVersion.class.getName());
    ms.setInstanceOf(true);
    settings.getMapperSettings().add(ms);
    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    IMapperVersion vrec = (IMapperVersion) event.getSource();
    VersionInfo vi = event.getAccessObject().getMapper().getVersionInfo();
    if (vi != null) {
      vrec.setMapperVersion(vi.version());
    }
    return Future.succeededFuture();
  }

}
