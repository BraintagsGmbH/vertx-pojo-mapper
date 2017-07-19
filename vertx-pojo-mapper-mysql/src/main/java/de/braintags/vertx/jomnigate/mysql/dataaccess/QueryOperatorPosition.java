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
package de.braintags.vertx.jomnigate.mysql.dataaccess;

import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;

/**
 * Defines to position of the {@link QueryOperator} in the SQL statement
 * <br>
 * Copyright: Copyright (c) 19.07.2017 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author mpluecker
 */
public enum QueryOperatorPosition {

  PREFIX,
  INFIX

}
