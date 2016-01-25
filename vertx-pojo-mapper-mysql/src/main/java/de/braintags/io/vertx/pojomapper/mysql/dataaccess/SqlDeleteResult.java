/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.DeleteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.ext.sql.UpdateResult;

/**
 * An extension for SQL, which contains the update result information
 * 
 * @author Michael Remme
 * 
 */

public class SqlDeleteResult extends DeleteResult {
  private UpdateResult updateResult;

  /**
   * @param datastore
   *          the {@link IDataStore} where the result was created by
   * @param mapper
   *          the underlaying {@link IMapper}
   * @param command
   *          the native command for the datastore
   * @param ur
   *          {@link UpdateResult} from the datastore
   */
  public SqlDeleteResult(IDataStore datastore, IMapper mapper, Object command, UpdateResult ur) {
    super(datastore, mapper, command);
    this.updateResult = ur;
  }

  /**
   * Get the {@link UpdateResult} which was returned by a delete execution
   * 
   * @return
   */
  public UpdateResult getUpdateResult() {
    return updateResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getDeletedInstances()
   */
  @Override
  public int getDeletedInstances() {
    return updateResult.getUpdated();
  }

}
