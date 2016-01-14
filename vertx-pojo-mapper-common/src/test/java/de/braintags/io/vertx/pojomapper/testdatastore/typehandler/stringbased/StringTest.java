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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.stringbased;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import io.vertx.core.json.JsonObject;
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
  }

  @Test
  public void testCalendar(TestContext context) throws Exception {
    Calendar source = Calendar.getInstance();
    String str = "TestKey";
    toString(context, str, source);
    fromString(context, str, source);
  }

  @Test
  public void testDate(TestContext context) throws Exception {
    Timestamp source = new Timestamp(System.currentTimeMillis());
    String str = "TestKey";
    toString(context, str, source);
    fromString(context, str, source);
  }

}

/*
 * getDefinedTypeHandlers().add(new CalendarTypeHandler(this));
 * 
 */