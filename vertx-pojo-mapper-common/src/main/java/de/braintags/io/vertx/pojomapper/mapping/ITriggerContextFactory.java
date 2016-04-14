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
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * This factory is used to create instances of {@link ITriggerContext} which are then used for those
 * mapper methods which are annotated by lifecycles like {@link BeforeSave} etc.
 * 
 * @author Michael Remme
 * 
 */
@FunctionalInterface
public interface ITriggerContextFactory {

  /**
   * Creates a new instance of {@link ITriggerContext} before a defined lifecycle method is called
   * 
   * @param mapper
   *          the mapper, which is handled
   * @param handler
   *          the handler to be informed
   * 
   * @return
   */
  ITriggerContext createTriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler);
}
