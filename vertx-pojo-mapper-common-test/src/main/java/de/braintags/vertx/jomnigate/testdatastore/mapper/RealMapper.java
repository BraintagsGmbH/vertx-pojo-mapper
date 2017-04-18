/*-
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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
public class RealMapper extends BaseRecord {

  private String text;
  private int number;
  private Date date;

  @JsonCreator
  protected RealMapper(@JsonProperty("text") String text, @JsonProperty("number") int number,
      @JsonProperty("date") Date date) {
    this.text = text;
    this.number = number;
    this.date = date;
  }

  public static RealMapper createRealMapper(String text, int number, Date date) {
    return new RealMapper(text, number, date);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
