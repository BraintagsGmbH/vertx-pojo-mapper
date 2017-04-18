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
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
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
    @IndexField(fieldName = "weight") }, name = "testIndex"))
public class PersonWithAnimals {

  @Id
  public String idField;
  private String name;

  public String secName;

  public Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

  public Animal rabbit;

  public Map<Integer, Double> myMap;

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

  @Embedded
  public Animal animal;
  @Embedded
  public Animal chicken;
  @Embedded
  public List<Animal> chickenFarm;
  @Embedded
  public List<Animal> dogFarm;
  @Embedded
  public Map<Integer, Animal> myMapEmbedded;
  @Embedded
  public Map<Integer, Animal> myMapEmbedded2;
  @Embedded
  public Animal[] animalArrayEmbedded;
  @Embedded
  public Animal[] animalArrayEmbedded2;

  @Ignore
  public String ignoreField = "";
  @Ignore
  private String ignoreField2 = "";

  /**
   * 
   */
  public PersonWithAnimals() {
  }

  /**
   * 
   */
  public PersonWithAnimals(final int count) {
    this.name = "name " + count;
    this.animal = new Animal(0);

    this.animalArrayEmbedded = new Animal[10];
    for (int i = 0; i < 10; i++) {
      this.animalArrayEmbedded[i] = new Animal(i);
    }
    this.animalArrayEmbedded2 = new Animal[10];
    for (int i = 0; i < 10; i++) {
      this.animalArrayEmbedded2[i] = new Animal(i);
    }
    this.chicken = new Animal(5);
    this.dogFarm = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      this.dogFarm.add(new Animal(i));
    }

    this.intValue = 50;
    this.listWithConstructor = createList("constr ", 8);
    this.myClass = Double.class;
    this.myMap = initMap(5);
    this.myMapEmbedded = initAnimalMap(6);
    this.myMapEmbedded2 = initAnimalMap(9);
    this.secName = "sec";
    this.stories = createList("story ", 7);
    this.stringArray = createArray("story2 ", 5);
    this.timeStamp = new Timestamp(System.currentTimeMillis());
    this.weight = new Double(788.56);
  }

  private static Map<Integer, Animal> initAnimalMap(final int count) {
    Map<Integer, Animal> map = new HashMap<>();
    for (int i = 0; i < count; i++) {
      map.put(new Integer(i), new Animal(i));
    }
    return map;
  }

  private static Map<Integer, Double> initMap(final int count) {
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
  public void setName(final String name) {
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
  public void setIgnoreField2(final String ignoreField2) {
    this.ignoreField2 = ignoreField2;
  }

  private static final ArrayList<String> createList(final String prefix, final int counter) {
    ArrayList<String> returnList = new ArrayList<>();
    for (int i = 0; i < counter; i++) {
      returnList.add(prefix + counter);
    }
    return returnList;
  }

  private static final String[] createArray(final String prefix, final int counter) {
    String[] rs = new String[counter];
    for (int i = 0; i < counter; i++) {
      rs[i] = prefix + counter;
    }
    return rs;
  }

}
