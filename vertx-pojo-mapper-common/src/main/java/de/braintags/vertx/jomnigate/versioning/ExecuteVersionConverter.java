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
import java.util.Iterator;
import java.util.List;

import de.braintags.vertx.jomnigate.annotation.VersionConverterDefinition;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.impl.AbstractObserver;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * This observer implementation is used to execute version conversion for those instances, where a {@link VersionInfo}
 * is defined. It is created and added into the observer list automatically by the mapper, when {@link VersionInfo} is
 * found.
 * 
 * @author Michael Remme
 * 
 */
public class ExecuteVersionConverter extends AbstractObserver {
  private List<ConverterEntry> converterList = new ArrayList<>();
  private long currentVersion;

  /**
   * Creates a new instance based on the information of the {@link VersionInfo}
   * 
   * @param versionInfo
   */
  public ExecuteVersionConverter(VersionInfo versionInfo) {
    this.currentVersion = versionInfo.version();
    for (VersionConverterDefinition vcd : versionInfo.versionConverter()) {
      try {
        converterList.add(new ConverterEntry(vcd.destinationVersion(), vcd.converter().newInstance()));
      } catch (InstantiationException | IllegalAccessException e) {
        throw new MappingException(e);
      }
    }
    Collections.sort(converterList);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserver#canHandleEvent(de.braintags.vertx.jomnigate.observer.
   * IObserverEvent, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return !converterList.isEmpty() && ((IMapperVersion) event.getSource()).getMapperVersion() < currentVersion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    IMapperVersion record = (IMapperVersion) event.getSource();
    Iterator<ConverterEntry> entries = converterList.stream()
        .filter(con -> con.destinationVersion > record.getMapperVersion()).iterator();
    if (entries.hasNext()) {
      List<Future> fl = new ArrayList<>();
      while (entries.hasNext()) {
        try {
          ConverterEntry entry = entries.next();
          record.setMapperVersion(entry.destinationVersion);
          Future<Void> tmp = entry.converter.convert(event.getDataStore(), record, context);
          fl.add(tmp);
        } catch (Exception e) {
          return Future.failedFuture(e);
        }
      }
      CompositeFuture cf = CompositeFuture.all(fl);
      Future<Void> f = Future.future();
      cf.setHandler(res -> {
        if (res.failed()) {
          f.fail(res.cause());
        } else {
          f.complete();
        }
      });
      return f;
    } else {
      return Future.succeededFuture();
    }
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
