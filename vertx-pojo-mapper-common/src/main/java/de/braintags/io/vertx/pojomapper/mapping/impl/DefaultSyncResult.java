/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import de.braintags.io.vertx.pojomapper.mapping.SyncAction;

/**
 * The default implementation for all datastores, where String is used as native format to synchronize a connected table
 * 
 * @author Michael Remme
 * 
 */

public class DefaultSyncResult implements ISyncResult<String> {
  private String syncCommand = null;
  private SyncAction action;

  /**
   * Creates a new instance with the given {@link SyncAction}
   * 
   * @action the type od {@link SyncAction}
   */
  public DefaultSyncResult(SyncAction action) {
    this.action = action;
  }

  /**
   * Creates a new instance with the given command
   * 
   * @action the type od {@link SyncAction}
   * @param syncCommand
   *          the command to be set
   */
  public DefaultSyncResult(SyncAction action, String syncCommand) {
    this(action);
    this.syncCommand = syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.mapping.ISyncResult#getSyncCommand()
   */
  @Override
  public String getSyncCommand() {
    return syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.mapping.ISyncResult#getAction()
   */
  @Override
  public SyncAction getAction() {
    return action;
  }

  /**
   * Set the action, which was performed by a synchronization
   * 
   * @param action
   *          the action
   */
  public void setAction(SyncAction action) {
    this.action = action;
  }

  @Override
  public String toString() {
    return syncCommand + ": " + action;
  }
}
