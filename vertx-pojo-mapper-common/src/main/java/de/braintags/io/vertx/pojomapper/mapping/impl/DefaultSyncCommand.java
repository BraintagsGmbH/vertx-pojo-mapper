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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.mapping.ISyncCommand;
import de.braintags.io.vertx.pojomapper.mapping.SyncAction;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DefaultSyncCommand implements ISyncCommand<String> {
  private String syncCommand = null;
  private SyncAction action;

  /**
   * Creates a new instance with the given {@link SyncAction}
   * 
   * @action the type od {@link SyncAction}
   */
  public DefaultSyncCommand(SyncAction action) {
    this.action = action;
  }

  /**
   * Creates a new instance with the given command
   * 
   * @action the type od {@link SyncAction}
   * @param syncCommand
   *          the command to be set
   */
  public DefaultSyncCommand(SyncAction action, String syncCommand) {
    this(action);
    this.syncCommand = syncCommand;
  }

  @Override
  public String getCommand() {
    return syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#getAction()
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
