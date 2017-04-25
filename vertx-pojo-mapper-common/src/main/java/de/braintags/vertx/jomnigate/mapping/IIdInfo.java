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
package de.braintags.vertx.jomnigate.mapping;

import de.braintags.vertx.jomnigate.dataaccess.query.IdField;

/**
 * Contains general info about the ID of an {@link IProperty}
 * 
 * @author sschmitt
 *
 */
public interface IIdInfo {

  /**
   * Get the {@link IProperty} instance of the ID field
   * 
   * @return the mapped property
   */
  IProperty getField();

  /**
   * Get the {@link IdField} instance for this mapper to make queries directly of the ID field
   * 
   * @return the id field
   */
  IdField getIndexedField();

}
