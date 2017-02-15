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

package de.braintags.vertx.jomnigate.dataaccess.delete;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * The result of the execution of an {@link IDelete}
 * 
 * @author Michael Remme
 * 
 */
public interface IDeleteResult {

  /**
   * Get the {@link IDataStore} by which the current instance was created
   * 
   * @return
   */
  public IDataStore getDataStore();

  /**
   * Get the underlaying {@link IMapper}
   * 
   * @return
   */
  public IMapper getMapper();

  /**
   * Get the original command, which was executed in the datastore to perform the delete action
   * 
   * @return the command
   */
  public Object getOriginalCommand();

  /**
   * Get the number of instances which were deleted
   * 
   * @return the number of deleted instances
   */
  public int getDeletedInstances();

}
