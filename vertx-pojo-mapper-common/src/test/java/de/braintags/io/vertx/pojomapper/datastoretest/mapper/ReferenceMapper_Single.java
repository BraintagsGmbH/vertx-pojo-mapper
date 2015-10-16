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
package de.braintags.io.vertx.pojomapper.datastoretest.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

public class ReferenceMapper_Single {
  @Id
  public String id;
  @Referenced
  public SimpleMapper simpleMapper;

  /**
   * 
   */
  public ReferenceMapper_Single() {
  }

  @Override
  public boolean equals(Object o) {
    ReferenceMapper_Single om = (ReferenceMapper_Single) o;
    boolean equal = om.id.equals(id);
    boolean equal2 = om.simpleMapper.equals(simpleMapper);
    return equal && equal2;
  }

}
