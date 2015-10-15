/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.DeleteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoDeleteResult extends DeleteResult {

  /**
   * @param datastore
   * @param mapper
   * @param command
   */
  public MongoDeleteResult(IDataStore datastore, IMapper mapper, Object command) {
    super(datastore, mapper, command);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getDeletedInstances()
   */
  @Override
  public int getDeletedInstances() {
    return 0;
  }

}
