package de.braintags.io.vertx.pojomapper.dataaccess.query;

import java.util.List;

/**
 * A container that connects multiple search conditions<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public interface ISearchConditionContainer extends ISearchCondition {

  /**
   * @return the conditions of this container
   */
  public List<ISearchCondition> getConditions();

  /**
   * @return the connecting logic for the contents of this container
   */
  public QueryLogic getQueryLogic();

}
