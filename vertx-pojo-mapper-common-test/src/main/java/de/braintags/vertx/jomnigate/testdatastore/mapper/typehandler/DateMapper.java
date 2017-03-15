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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

import de.braintags.vertx.jomnigate.annotation.Entity;

/**
 * Mapper for testing boolean
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class DateMapper extends BaseRecord {
  public Time myTime = new Time(System.currentTimeMillis());
  public Timestamp myTimeStamp = new Timestamp(System.currentTimeMillis());
  public Date javaDate = new Date(System.currentTimeMillis());
  public java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
  public DateTime jodaDate = new DateTime(System.currentTimeMillis());

}
