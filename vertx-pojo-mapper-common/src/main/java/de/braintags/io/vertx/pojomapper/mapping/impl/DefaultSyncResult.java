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

/**
 * The default implementation for all datastores, where String is used as native format to synchronize a connected table
 * 
 * @author Michael Remme
 * 
 */

public class DefaultSyncResult implements ISyncResult<String> {
  private String syncCommand = null;

  /**
   * 
   */
  public DefaultSyncResult() {
  }

  /**
   * Creates a new instance with the given command
   * 
   * @param syncCommand
   *          the command to be set
   */
  public DefaultSyncResult(String syncCommand) {
    this.syncCommand = syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#getSyncCommand()
   */
  @Override
  public String getSyncCommand() {
    return syncCommand;
  }

}
