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
package de.braintags.vertx.jomnigate.testdatastore.typehandler.stringbased;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import de.braintags.vertx.jomnigate.datatypes.Price;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.CalendarTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.DateTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.TimeTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.TimestampTypeHandler;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class StringTest extends AbstractStringTypehandlerTest {

  @Test
  public void testBoolean(TestContext context) {
    fromString(context, "true", Boolean.TRUE);
    fromString(context, "false", Boolean.FALSE);
    fromString(context, "nix", Boolean.FALSE);
    fromString(context, "0", Boolean.FALSE);
    fromString(context, "1", Boolean.FALSE);
    toString(context, "true", Boolean.TRUE);
    toString(context, "false", Boolean.FALSE);
  }

  @Test
  public void testJson(TestContext context) {
    String jsonString = "{\"testkey\":\"testvalue\"}";
    JsonObject json = new JsonObject();
    json.put("testkey", "testvalue");
    toString(context, jsonString, json);
    fromString(context, jsonString, json);
  }

  @Test
  public void testDouble(TestContext context) {
    fromString(context, "", new Double("0"));
    fromString(context, " ", new Double("0"));
    fromString(context, "12.88", new Double(12.88));
    fromString(context, "12,88", new Double(12.88));
    fromString(context, "1.2,88", new Double(12.88));
    toString(context, "12.88", new Double(12.88));
  }

  @Test
  public void testPrice(TestContext context) {
    fromString(context, "", new Price("0"));
    fromString(context, " ", new Price("0"));
    fromString(context, "12.88", new Price("12.88"));
    fromString(context, "12,88", new Price("12.88"));
    fromString(context, "1.2,88", new Price("12.88"));
    toString(context, "12.88", new Price("12.88"));
  }

  @Test
  public void testBigDecimal(TestContext context) {
    fromString(context, "", new BigDecimal("0"));
    fromString(context, " ", new BigDecimal("0"));
    fromString(context, "12.88", new BigDecimal("12.88"));
    fromString(context, "12,88", new BigDecimal("12.88"));
    fromString(context, "1.2,88", new BigDecimal("12.88"));
    toString(context, "12.88", new BigDecimal("12.88"));
  }

  @Test
  public void testBigInteger(TestContext context) {
    fromString(context, "", new BigInteger("0"));
    fromString(context, " ", new BigInteger("0"));
    fromString(context, "12", new BigInteger("12"));
    toString(context, "12", new BigInteger("12"));
  }

  @Test
  public void testFloat(TestContext context) {
    fromString(context, "", new Float("0"));
    fromString(context, " ", new Float("0"));
    fromString(context, "12.88", new Float("12.88"));
    fromString(context, "12,88", new Float("12.88"));
    fromString(context, "1.2,88", new Float("12.88"));
    toString(context, "12.88", new Float("12.88"));
  }

  @Test
  public void testShort(TestContext context) {
    fromString(context, "", new Short("0"));
    fromString(context, " ", new Short("0"));
    fromString(context, "12", new Short("12"));
    toString(context, "12", new Short("12"));
  }

  @Test
  public void testInteger(TestContext context) {
    fromString(context, "", new Integer("0"));
    fromString(context, " ", new Integer("0"));
    fromString(context, "12", new Integer("12"));
    toString(context, "12", new Integer("12"));
  }

  @Test
  public void testLong(TestContext context) {
    fromString(context, "", new Long("0"));
    fromString(context, " ", new Long("0"));
    fromString(context, "12", new Long("12"));
    toString(context, "12", new Long("12"));
  }

  @Test
  public void testCharacter(TestContext context) {
    Character source = new Character('a');
    String str = "a";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testCharSequence(TestContext context) {
    String source = new String("test");
    String str = "test";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testByte(TestContext context) {
    Byte source = new Byte("1");
    String str = "1";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testURI(TestContext context) {
    URI source = URI.create("http://www.braintags.de");
    String str = "http://www.braintags.de";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testURL(TestContext context) throws Exception {
    URL source = URI.create("http://www.braintags.de").toURL();
    String str = "http://www.braintags.de";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testClass(TestContext context) throws Exception {
    Class source = ITypeHandler.class;
    String str = ITypeHandler.class.getName();
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testLocale(TestContext context) throws Exception {
    Locale source = Locale.CANADA;
    String str = Locale.CANADA.toString();
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testEnum(TestContext context) throws Exception {
    TestEnum source = TestEnum.TestKey;
    String str = "TestKey";
    toString(context, str, source);
    fromString(context, str, source);

    source = null;
    str = "";
    toString(context, null, source, TestEnum.class);
    fromString(context, str, source, TestEnum.class);
  }

  @Test
  public void testCalendar(TestContext context) throws Exception {
    Calendar source = Calendar.getInstance();
    source.set(2016, 0, 3, 14, 50, 22);
    source.set(Calendar.MILLISECOND, 55);
    checkTypeHandler(context, source.getClass(), CalendarTypeHandler.class);
    String str = "2016-01-03 14:50:22.055";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testDate(TestContext context) throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.set(2016, 0, 3, 14, 50, 22);
    cal.set(Calendar.MILLISECOND, 55);
    String str = "2016-01-03";
    Date source = new Date(cal.getTimeInMillis());
    checkTypeHandler(context, source.getClass(), DateTypeHandler.class);
    toString(context, str, source);
    fromStringDate(context, str, source);
  }

  @Test
  public void testTime(TestContext context) throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.set(2016, 0, 3, 14, 50, 22);
    cal.set(Calendar.MILLISECOND, 55);
    String str = "14:50:22";
    Time source = new Time(cal.getTimeInMillis());
    checkTypeHandler(context, source.getClass(), TimeTypeHandler.class);
    toString(context, str, source);
    fromStringTime(context, str, source);
  }

  @Test
  public void testTimestamp(TestContext context) throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.set(2016, 0, 3, 14, 50, 22);
    cal.set(Calendar.MILLISECOND, 55);
    String str = "2016-01-03 14:50:22.055";
    Timestamp source = new Timestamp(cal.getTimeInMillis());
    checkTypeHandler(context, source.getClass(), TimestampTypeHandler.class);
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testGeoPoint(TestContext context) {
    GeoPoint point = new GeoPoint(new Position(14, 13));
    String expectedString = "{\"type\":\"Point\",\"coordinates\":[14.0,13.0]}";
    toString(context, expectedString, point);
    fromString(context, expectedString, point);
  }

  protected void fromStringDate(TestContext context, String str, Object expected) {
    Async async = context.async();
    ITypeHandler th = thf.getTypeHandler(expected.getClass(), null);
    ResultObject<Object> ro = new ResultObject<>(null);
    th.fromStore(str, null, expected.getClass(), res -> {
      if (res.failed()) {
        context.fail(res.cause());
        async.complete();
      } else {
        ro.setResult(res.result().getResult());
        async.complete();
      }
    });
    async.await();
    Object created = ro.getResult();
    checkEqualsDate(context, (Date) expected, (Date) created);
  }

  protected void checkEqualsDate(TestContext context, Date expected, Date created) {
    context.assertEquals(expected.getYear(), created.getYear());
    context.assertEquals(expected.getDate(), created.getDate());
    context.assertEquals(expected.getMonth(), created.getMonth());
  }

  protected void fromStringTime(TestContext context, String str, Object expected) {
    Async async = context.async();
    ITypeHandler th = thf.getTypeHandler(expected.getClass(), null);
    ResultObject<Object> ro = new ResultObject<>(null);
    th.fromStore(str, null, expected.getClass(), res -> {
      if (res.failed()) {
        context.fail(res.cause());
        async.complete();
      } else {
        ro.setResult(res.result().getResult());
        async.complete();
      }
    });
    async.await();
    Object created = ro.getResult();
    checkEqualsTime(context, expected, created);
  }

  protected void checkEqualsTime(TestContext context, Object expected, Object created) {
    context.assertEquals(expected.toString(), created.toString());
  }

}

/*
 * getDefinedTypeHandlers().add(new CalendarTypeHandler(this));
 * 
 */
