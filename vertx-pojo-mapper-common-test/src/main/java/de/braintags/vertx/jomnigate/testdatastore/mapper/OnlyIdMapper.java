/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;

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
