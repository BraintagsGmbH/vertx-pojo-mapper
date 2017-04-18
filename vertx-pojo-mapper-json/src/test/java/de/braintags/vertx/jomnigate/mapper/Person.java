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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Ignore;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad;
import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedField;

/**
 * Person is used as mapper class
 * 
 * @author Michael Remme
 * 
 */

@Entity(name = "PersonColumn")
@Indexes(@Index(fields = { @IndexField(fieldName = "name"),
    @IndexField(fieldName = "weight") }, name = "testIndex"))
public class Person extends AbstractPerson {
  public static final IIndexedField NAME = new IndexedField("name");
  public static final IIndexedField SEC_NAME = new IndexedField("secName");
  public static final IIndexedField WEIGHT = new IndexedField("weight");

  public static final int NUMBER_OF_PROPERTIES = 23;

  @Id
  public String idField;
  private String name;

  public String secName;

  @Referenced
  public Animal animal;

  @Embedded
  public Animal chicken;

  @Embedded
  public List<Animal> chickenFarm;

  @Referenced
  public List<Animal> dogFarm;

  public Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

  public Animal rabbit;

  public Map<Integer, Double> myMap;
  @Embedded
  public Map<Integer, Animal> myMapEmbedded;
  @Referenced
  public Map<Integer, Animal> myMapReferenced;

  public Animal[] animalArray;
  @Embedded
  public Animal[] animalArrayEmbedded;
  @Referenced
  public Animal[] animalArrayReferenced;

  public Class<? extends Double> myClass;

  public Collection<String> stories;

  public List<Animal> listAnimals;

  public List unknownSubType;

  public ArrayList<String> listWithConstructor;

  @Property("WEIGHT")
  public Double weight;

  public int intValue;

  private String hiddenString;
  public transient String transientString;
  public String[] stringArray = { "eins", "zwei", "drei" };

  @Ignore
  public String ignoreField = "";
  @Ignore
  private String ignoreField2 = "";

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
  public void setName(final String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IPerson#beforeLoadFromInterface()
   */
  @Override
  @BeforeLoad
  public void beforeLoadFromInterface() {
    System.out.println("handleBeforeLoad");
  }

  /**
   * @return the ignoreField2
   */
  public String getIgnoreField2() {
    return ignoreField2;
  }

  /**
   * @param ignoreField2
   *          the ignoreField2 to set
   */
  public void setIgnoreField2(final String ignoreField2) {
    this.ignoreField2 = ignoreField2;
  }

}
