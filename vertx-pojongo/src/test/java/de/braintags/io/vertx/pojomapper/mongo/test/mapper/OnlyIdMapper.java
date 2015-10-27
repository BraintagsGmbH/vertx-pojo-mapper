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
package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;

/**
 * Mapper to test extremes. ID field is required by mapping, thus a complete empty Mapper is not possible
 *
 * @author Michael Remme
 * 
 */

@Entity
public class OnlyIdMapper {
  @Id
  public String id;

  /**
   * 
   */
  public OnlyIdMapper() {
  }

}
