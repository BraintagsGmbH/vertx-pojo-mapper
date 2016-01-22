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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.FutureImpl;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A TriggerHandler can be used as argument for mapper methods, which are annotated by one of the annotations like
 * {@link BeforeSave}, {@link AfterSave} etc.
 * 
 * @author Michael Remme
 * 
 */
public class TriggerHandler extends FutureImpl<Void> implements ITriggerHandler {
  private IMapper mapper;
  private Handler<AsyncResult<Void>> handler;

  /**
   * 
   */
  public TriggerHandler(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    this.mapper = mapper;
    this.handler = handler;
  }

  /**
   * Get the instance of IMapper, which is underlaying the current request
   * 
   * @return the mapper
   */
  @Override
  public final IMapper getMapper() {
    return mapper;
  }

}
