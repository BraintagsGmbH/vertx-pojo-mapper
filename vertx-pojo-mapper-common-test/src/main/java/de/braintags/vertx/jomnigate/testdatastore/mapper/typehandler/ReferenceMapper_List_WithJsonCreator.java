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
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.testdatastore.mapper.RealMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class ReferenceMapper_List_WithJsonCreator extends BaseRecord {

  @Referenced
  public List<SimpleMapper> simpleMapper;
  @Referenced
  private List<RealMapper> realMapper;

  @JsonCreator
  protected ReferenceMapper_List_WithJsonCreator(@JsonProperty("simpleMapper") List<SimpleMapper> simpleMapper,
      @JsonProperty("realMapper") List<RealMapper> realMapper) {
    super();
    this.simpleMapper = simpleMapper;
    this.realMapper = realMapper;
  }

  public static ReferenceMapper_List_WithJsonCreator createReferenceMapper_List(List<SimpleMapper> simpleMapper,
      List<RealMapper> realMapper) {
    return new ReferenceMapper_List_WithJsonCreator(simpleMapper, realMapper);
  }

  public static ReferenceMapper_List_WithJsonCreator createReferenceMapper_List(int numberOfSubRecords) {
    List<SimpleMapper> simpleMapper = new ArrayList<>();
    for (int i = 0; i < numberOfSubRecords; i++) {
      simpleMapper.add(new SimpleMapper("referencedMapperList " + i, "sec prop " + i));
    }

    List<RealMapper> realMapper = new ArrayList<>();
    for (int i = 0; i < numberOfSubRecords; i++) {
      realMapper.add(RealMapper.createRealMapper("referencedRealMapper " + i, i,
          new Date(System.currentTimeMillis() + i * TimeUnit.HOURS.toMillis(1))));
    }
    return new ReferenceMapper_List_WithJsonCreator(simpleMapper, realMapper);
  }

  public List<RealMapper> getRealMapper() {
    if (realMapper == null)
      realMapper = new ArrayList<>();
    return realMapper;
  }

  public void setRealMapper(List<RealMapper> realMapper) {
    this.realMapper = realMapper;
  }

}
