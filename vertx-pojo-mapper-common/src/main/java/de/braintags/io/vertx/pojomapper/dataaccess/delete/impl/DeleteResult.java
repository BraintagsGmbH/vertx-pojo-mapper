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

package de.braintags.io.vertx.pojomapper.dataaccess.delete.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * The result of the execution of an {@link IDelete}
 * 
 * @author Michael Remme
 * 
 */

public abstract class DeleteResult implements IDeleteResult {
  private IDataStore datastore;
  private IMapper mapper;
  private Object command;

  /**
   * 
   * @param datastore
   *          the parent datastore by which the delete was executed
   * @param mapper
   *          the underlaying mapper
   * @param command
   *          the native command executed
   */
  public DeleteResult(IDataStore datastore, IMapper mapper, Object command) {
    this.datastore = datastore;
    this.mapper = mapper;
    this.command = command;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getOriginalCommand()
   */
  @Override
  public Object getOriginalCommand() {
    return command;
  }

}
