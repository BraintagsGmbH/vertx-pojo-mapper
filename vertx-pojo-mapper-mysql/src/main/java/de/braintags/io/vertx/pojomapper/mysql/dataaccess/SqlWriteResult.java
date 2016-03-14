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

import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlWriteResult extends WriteResult {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult#addEntry(de.braintags.io.vertx.pojomapper.
   * mapping.IStoreObject, java.lang.Object, de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction)
   */
  @Override
  public synchronized void addEntry(IStoreObject<?> sto, Object id, WriteAction action) {
    super.addEntry(sto, id, action);
  }

}
