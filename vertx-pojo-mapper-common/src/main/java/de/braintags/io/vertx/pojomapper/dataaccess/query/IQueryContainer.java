package de.braintags.io.vertx.pojomapper.dataaccess.query;

import java.util.List;

/**
 * A query container that connects multiple query parts<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public interface IQueryContainer extends IQueryPart {

  /**
   * @return the query parts contained inside this container
   */
  public List<IQueryPart> getContent();

  /**
   * @return the connecting logic for the contents of this container
   */
  public QueryLogic getConnector();

}
