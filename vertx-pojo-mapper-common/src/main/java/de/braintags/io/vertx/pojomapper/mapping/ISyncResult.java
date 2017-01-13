/*-
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

import java.util.List;

/**
 * ISyncResult is the result, which is returned by the method
 * {@link IDataStoreSynchronizer#synchronize(IMapper, io.vertx.core.Handler)} and which contains all relevant
 * information for the synchronization process
 * 
 * @author Michael Remme
 * 
 * @param <T>
 *          the dataformat which is used as sync command
 * 
 */
public interface ISyncResult<T> {

  /**
   * Get the list of commands, which were processed during initialization
   * 
   * @return
   */
  List<ISyncCommand<T>> getCommands();

  /**
   * Add a new command
   * 
   * @param command
   *          the command to be added
   */
  void addCommand(ISyncCommand<T> command);

  /**
   * Returns true, if during initialization no modification on the underlaying table / collection was needed
   * 
   * @return true, if no modification needed
   */
  boolean isUnmodified();
}
