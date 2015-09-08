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

import java.util.Collection;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.test.mapper.AbstractPerson;
import de.braintags.io.vertx.pojomapper.test.mapper.Animal;

/**
 * Person is used as mapper class
 * 
 * @author Michael Remme
 * 
 */

public class Person extends AbstractPerson {
  @Id
  public String idField;
  private String name;
  public String secName;

  @Referenced
  public Animal animal;

  @Embedded
  public Animal chicken;

  public Map<String, Object> myMap;

  public Class<? extends Double> myClass;

  public Collection<String> stories;

  @Property("WEIGHT")
  public Double weight;

  private String hiddenString;
  public transient String transientString;

  /**
   * @return the name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IPerson#beforeLoadFromInterface()
   */
  @Override
  @BeforeLoad
  public void beforeLoadFromInterface() {
    System.out.println("handleBeforeLoad");
  }

}
