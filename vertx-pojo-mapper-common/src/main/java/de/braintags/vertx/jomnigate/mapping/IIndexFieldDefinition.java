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

import de.braintags.vertx.jomnigate.annotation.IndexType;

/**
 * Defines a single field of an {@link IIndexDefinition}
 * 
 * @author sschmitt
 *
 */
public interface IIndexFieldDefinition {

  /**
   * The name of the column to index
   * 
   * @return the name
   */
  public String getName();

  /**
   * The type of this index field
   * 
   * @return the type
   */
  public IndexType getType();
}
