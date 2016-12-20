package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;

/**
 * <br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public class QueryAnd extends AbstractQueryContainer {

  public QueryAnd(IQueryPart... queryParts) {
    super(queryParts);
  }

  /*
   * (non-Javadoc)
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getConnector()
   */
  @Override
  public QueryLogic getConnector() {
    return QueryLogic.AND;
  }

}
