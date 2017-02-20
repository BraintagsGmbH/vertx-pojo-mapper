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
package de.braintags.vertx.jomnigate.mongo.performance.mapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.IndexOptions;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Ignore;
import de.braintags.vertx.jomnigate.annotation.field.Property;

/**
 * Person is used as mapper class
 * 
 * @author Michael Remme
 * 
 */

@Entity(name = "PersonColumn")
@Indexes(@Index(fields = { @IndexField(fieldName = "name"),
    @IndexField(fieldName = "weight") }, name = "testIndex", options = @IndexOptions(unique = false)))
public class PersonPure {

  @Id
  public String idField;
  private String name;
  public String secName;

  public Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
  public String rabbit;
  public String[] animalArray;
  public Class<? extends Double> myClass;
  public Collection<String> stories;
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

  // embedded properties or non embedded as object typehandler
  // @Embedded
  public String animal;
  // @Embedded
  public String chicken;
  // @Embedded
  public List<String> chickenFarm;
  // @Embedded
  public List<String> dogFarm;
  // @Embedded
  public String[] animalArrayEmbedded;
  // @Embedded
  public String[] animalArrayEmbedded2;

  /**
   * 
   */
  public PersonPure() {
  }

  /**
   * 
   */
  public PersonPure(int count) {
    this.name = "name " + count;
    this.animal = "animal";
    this.animalArray = new String[10];
    for (int i = 0; i < 10; i++) {
      this.animalArray[i] = "animal " + i;
    }
    this.animalArrayEmbedded = new String[10];
    for (int i = 0; i < 10; i++) {
      this.animalArrayEmbedded[i] = "animal " + i;
    }
    this.animalArrayEmbedded2 = new String[10];
    for (int i = 0; i < 10; i++) {
      this.animalArrayEmbedded2[i] = "animal " + i;
    }
    this.chicken = "chicke";
    this.dogFarm = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      this.dogFarm.add("dog " + i);
    }

    this.intValue = 50;
    this.listWithConstructor = createList("constr ", 8);
    this.myClass = Double.class;
    this.secName = "sec";
    this.stories = createList("story ", 7);
    this.stringArray = createArray("story2 ", 5);
    this.timeStamp = new Timestamp(System.currentTimeMillis());
    this.weight = new Double(788.56);
  }

  private static Map<Integer, Animal> initAnimalMap(int count) {
    Map<Integer, Animal> map = new HashMap<>();
    for (int i = 0; i < count; i++) {
      map.put(new Integer(i), new Animal(i));
    }
    return map;
  }

  private static Map<Integer, Double> initMap(int count) {
    Map<Integer, Double> map = new HashMap<>();
    for (int i = 0; i < count; i++) {
      map.put(new Integer(i), new Double(i));
    }
    return map;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
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
  public void setIgnoreField2(String ignoreField2) {
    this.ignoreField2 = ignoreField2;
  }

  private static final ArrayList<String> createList(String prefix, int counter) {
    ArrayList<String> returnList = new ArrayList<>();
    for (int i = 0; i < counter; i++) {
      returnList.add(prefix + counter);
    }
    return returnList;
  }

  private static final String[] createArray(String prefix, int counter) {
    String[] rs = new String[counter];
    for (int i = 0; i < counter; i++) {
      rs[i] = prefix + counter;
    }
    return rs;
  }

}
