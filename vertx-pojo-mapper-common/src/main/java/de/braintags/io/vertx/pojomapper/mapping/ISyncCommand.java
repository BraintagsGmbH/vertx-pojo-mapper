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
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * Describes a synchronization step which was processed during initalization of a mapper
 * 
 * @author Michael Remme
 * 
 * @param <T>
 *          the dataformat which is used as sync command
 * 
 */
public interface ISyncCommand<T> {

  /**
   * Retrieve the native object which was used as command to synchronize the connected table / column of the
   * {@link IDataStore} If no synchronization was performed, then this will be null
   * 
   * @return the native object which was used to synchronize or null, if no synchronization was performed
   */
  public T getCommand();

  /**
   * Get the {@link SyncAction} which was performed by a synchronization
   * 
   * @return
   */
  public SyncAction getAction();

}
