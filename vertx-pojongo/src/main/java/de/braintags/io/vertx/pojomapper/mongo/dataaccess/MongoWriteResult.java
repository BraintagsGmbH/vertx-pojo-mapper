/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoWriteResult extends WriteResult {

  /**
   * 
   */
  public MongoWriteResult() {
    super();
  }

  /**
   * @param resultList
   */
  public MongoWriteResult(List<IWriteEntry> resultList) {
    super(resultList);
  }

}
