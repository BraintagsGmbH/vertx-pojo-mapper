/*-
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
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
