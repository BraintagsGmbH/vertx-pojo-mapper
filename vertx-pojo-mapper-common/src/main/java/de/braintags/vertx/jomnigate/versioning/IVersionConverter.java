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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.VersionConverterDefinition;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import io.vertx.core.Future;

/**
 * An IVersionConverter is used to convert instances of {@link IMapperVersion} from one version to a higher one. The
 * declaration is done by using the annotation {@link VersionInfo}, where inside the {@link VersionConverterDefinition}
 * contains the IVersionConverterClass, which shall be used.
 * The conversion is processed by the observer {@link ExecuteVersionConverter}, which will be automatically added for
 * all mapper declarations, which contain the annotation {@link VersionInfo}. If an instance is saved or loaded, where
 * the version {@link IMapperVersion#getMapperVersion()} is smaller than the current one, then the
 * ExecuteVersionConverter searches for all converter, which are defined to be executed and executes them.
 * 
 * @author Michael Remme
 * @param <T>
 *          the mapper class which will be handled
 */
public interface IVersionConverter<T> {

  /**
   * Convert an instance from one version to the next one
   * 
   * @param datastore
   * @param toBeConverted
   * @param context
   * @return
   */
  Future<Void> convert(IDataStore<?, ?> datastore, T toBeConverted, IObserverContext context);

}
