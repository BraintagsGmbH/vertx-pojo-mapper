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
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryRambler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation fills the contents into a {@link JsonObject} which then can be used as source for
 * {@link MongoClient#find(String, JsonObject, io.vertx.core.Handler)}
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryRambler extends AbstractQueryRambler {

  public MongoQueryRambler() {
    super(new MongoQueryExpression(), new QueryLogicTranslator(), new QueryOperatorTranslator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryRambler#translateValue(de.braintags.io.vertx.
   * util.
   * pojomapper.dataaccess.query.QueryOperator, java.lang.Object)
   */
  @Override
  protected Object translateValue(QueryOperator operator, Object value) {
    switch (operator) {
    case CONTAINS:
      return ".*" + value + ".*";

    case STARTS:
      return value + ".*";

    case ENDS:
      return ".*" + value;

    default:
      return super.translateValue(operator, value);
    }

  }

}
