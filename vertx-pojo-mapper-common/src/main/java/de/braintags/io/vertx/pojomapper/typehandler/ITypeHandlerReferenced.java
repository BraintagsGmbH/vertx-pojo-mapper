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
package de.braintags.io.vertx.pojomapper.typehandler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A special interface for alle ITypeHandler which are dealing with fields, annotated as {@link Referenced}. Those
 * ITypeHandler are storing an instance of {@link IObjectReference} in the first run, and in the second run those
 * references are resolved for the real objects to avoid hirarchical conflicts
 * 
 * @author Michael Remme
 * 
 */
public interface ITypeHandlerReferenced extends ITypeHandler {

  /**
   * Resolves the {@link IObjectReference} by its real object
   * 
   * @param store
   *          the store to be used
   * @param reference
   *          the {@link IObjectReference}
   * @param resultHandler
   *          the handler to be informed
   */
  void resolveReferencedObject(IDataStore store, IObjectReference reference,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler);

}
