/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapper;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class JsonMapper {
  @Id
  public String id;
  public String name;

  private JsonMapper() {
    name = "ttt";
  }

  public JsonMapper(String name) {
    this.name = name;
  }

  @Referenced
  public JsonSubMapper referencedSubmapper = new JsonSubMapper("uuu");

}
