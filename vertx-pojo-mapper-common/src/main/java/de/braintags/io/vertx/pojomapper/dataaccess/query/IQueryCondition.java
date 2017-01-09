package de.braintags.io.vertx.pojomapper.dataaccess.query;

/**
 * A single condition statement of a query<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */

public interface IQueryCondition extends IQueryPart {

  /**
   * @return the field of the condition
   */
  public String getField();

  /**
   * @return the operator of the condition
   */
  public QueryOperator getOperator();

  /**
   * @return the value of the condition
   */
  public Object getValue();

}