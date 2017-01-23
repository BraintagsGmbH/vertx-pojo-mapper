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
package de.braintags.vertx.jomnigate.dataaccess.write;

import de.braintags.vertx.jomnigate.mapping.IStoreObject;

/**
 * For each entity, which was saved by {@link IWrite#save(io.vertx.core.Handler)}, one instance of {@link IWriteEntry}
 * is created and stored inside {@link IWriteResult}
 * 
 * @author Michael Remme
 * 
 */

public interface IWriteEntry {

  /**
   * Get the instance of {@link IStoreObject}, which was created during the save action
   * 
   * @return the {@link IStoreObject} which was written
   */
  @SuppressWarnings("rawtypes")
  public IStoreObject getStoreObject();

  /**
   * Get the id of the saved object
   * 
   * @return the id of the instance
   */
  public Object getId();

  /**
   * Get the action, which was used when writing the given instance
   * 
   * @return the {@link WriteAction} used
   */
  public WriteAction getAction();
}
