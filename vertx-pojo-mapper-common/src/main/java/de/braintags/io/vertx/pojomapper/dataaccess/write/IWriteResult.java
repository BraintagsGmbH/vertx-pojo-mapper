/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.write;

import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * This object is created by a save action and contains the information about the action itself and the objects saved
 * 
 * 
 * @author Michael Remme
 *
 */
public interface IWriteResult {

  /**
   * The resulting list of {@link IWriteEntry}
   * 
   * @return
   */
  Iterator<IWriteEntry> iterator();

  /**
   * Add a new {@link IWriteEntry} from the given information
   * 
   * @param sto
   *          the {@link IStoreObject} which was handled
   * @param id
   *          the id, which was generated or existing already
   * @param action
   *          the {@link WriteAction} used
   */
  void addEntry(IStoreObject<?> sto, String id, WriteAction action);

  /**
   * Get the number of {@link IWriteEntry}
   * 
   * @return the size
   */
  int size();
}