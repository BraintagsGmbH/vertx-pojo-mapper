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
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
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
    @IndexField(fieldName = "weight") }, name = "testIndex"))
public class PersonWithAnimals_Mini {

  @Id
  public String idField;
  private String name;

  public Animal rabbit;
  public String[] stringArray = { "eins", "zwei", "drei" };

  // @Embedded
  public Animal animal;
  // @Embedded
  public Animal chicken;

  /**
   * 
   */
  public PersonWithAnimals_Mini() {
  }

  /**
   * 
   */
  public PersonWithAnimals_Mini(final int count) {
    this.name = "name " + count;
    this.animal = new Animal(0);
    this.chicken = new Animal(5);
    this.stringArray = createArray("story2 ", 5);
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
