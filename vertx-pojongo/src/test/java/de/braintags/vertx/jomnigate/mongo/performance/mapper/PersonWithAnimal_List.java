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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.IndexOptions;
import de.braintags.vertx.jomnigate.annotation.Indexes;
import de.braintags.vertx.jomnigate.annotation.field.Id;

/**
 * Person is used as mapper class
 * 
 * @author Michael Remme
 * 
 */

@Entity(name = "PersonColumn")
@Indexes(@Index(fields = { @IndexField(fieldName = "name"),
    @IndexField(fieldName = "weight") }, name = "testIndex", options = @IndexOptions(unique = false)))
public class PersonWithAnimal_List {

  @Id
  public String idField;
  private String name;

  // @Embedded
  public Animal animal;
  // @Embedded
  public Animal chicken;
  // @Embedded
  public List<Animal> chickenFarm;
  // @Embedded
  public List<Animal> dogFarm;

  /**
   * 
   */
  public PersonWithAnimal_List() {
  }

  /**
   * 
   */
  public PersonWithAnimal_List(int count) {
    this.name = "name " + count;
    this.animal = new Animal(0);

    this.chicken = new Animal(5);
    this.dogFarm = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      this.dogFarm.add(new Animal(i));
    }
    chickenFarm = createList("chicken farm ", 10);
    dogFarm = createList("dog farm ", 10);
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

  private static final ArrayList<Animal> createList(String prefix, int counter) {
    ArrayList<Animal> returnList = new ArrayList<>();
    for (int i = 0; i < counter; i++) {
      returnList.add(new Animal(counter));
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
