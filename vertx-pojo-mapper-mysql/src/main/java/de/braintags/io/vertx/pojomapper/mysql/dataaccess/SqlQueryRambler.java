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

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryRambler;

/**
 * First creates a query tree as JsonObject and then from the JsonObject the statement
 * 
 * @author Michael Remme
 * 
 */

public class SqlQueryRambler extends AbstractQueryRambler {

  public SqlQueryRambler() {
    super(new SqlExpression(), new QueryLogicTranslator(), new QueryOperatorTranslator());
  }

}
