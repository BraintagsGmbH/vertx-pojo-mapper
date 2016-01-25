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

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContext;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContextFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Default implementation of {@link ITriggerContextFactory}
 * 
 * @author Michael Remme
 * 
 */
public class TriggerContextFactory implements ITriggerContextFactory {

  /**
   * 
   */
  public TriggerContextFactory() {
  }

  @Override
  public ITriggerContext createTriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    return new TriggerContext(mapper, handler);
  }

}
