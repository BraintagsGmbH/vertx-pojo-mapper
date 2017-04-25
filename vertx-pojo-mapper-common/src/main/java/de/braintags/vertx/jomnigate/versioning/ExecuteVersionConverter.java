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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.braintags.vertx.jomnigate.annotation.VersionConverterDefinition;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import io.vertx.core.Future;

/**
 * This observer implementation is used to execute version conversion for those instances, where a {@link VersionInfo}
 * is defined. It is created and added into the observer list automatically by the mapper, when {@link VersionInfo} is
 * found.
 * 
 * @author Michael Remme
 * 
 */
public class ExecuteVersionConverter implements IObserver {
  private List<ConverterEntry> converter = new ArrayList<>();

  /**
   * Creates a new instance based on the information of the {@link VersionInfo}
   * 
   * @param versionInfo
   */
  public ExecuteVersionConverter(VersionInfo versionInfo) {
    for (VersionConverterDefinition vcd : versionInfo.versionConverter()) {
      try {
        converter.add(new ConverterEntry(vcd.destinationVersion(), vcd.converter().newInstance()));
      } catch (InstantiationException | IllegalAccessException e) {
        throw new MappingException(e);
      }
    }
    Collections.sort(converter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserver#canHandleEvent(de.braintags.vertx.jomnigate.observer.
   * IObserverEvent, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return true;
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
    return Future.failedFuture(new UnsupportedOperationException());
  }

  private class ConverterEntry implements Comparable<ConverterEntry> {
    private Long destinationVersion;
    private IVersionConverter converter;

    ConverterEntry(long destinationVersion, IVersionConverter converter) {
      this.destinationVersion = destinationVersion;
      this.converter = converter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ConverterEntry o) {
      return destinationVersion.compareTo(o.destinationVersion);
    }

  }

}
