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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.stringbased;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.StringTypeHandlerFactory;
import de.braintags.io.vertx.util.ResultObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public abstract class AbstractStringTypehandlerTest {

  @Rule
  public Timeout rule = Timeout.seconds(Integer.parseInt(System.getProperty("testTimeout", "20")));
  protected static ITypeHandlerFactory thf;

  @BeforeClass
  public static void init(TestContext context) {
    thf = new StringTypeHandlerFactory();
  }

  protected void toString(TestContext context, String expected, Object source) {
    toString(context, expected, source, source.getClass());
  }

  protected void toString(TestContext context, String expected, Object source, Class checkClass) {
    Async async = context.async();
    ITypeHandler th = thf.getTypeHandler(checkClass, null);
    ResultObject<Object> ro = new ResultObject<>(null);
    th.intoStore(source, null, res -> {
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
    checkEquals(context, expected, created);
  }

  protected void fromString(TestContext context, String str, Object expected) {
    fromString(context, str, expected, expected.getClass());
  }

  protected void fromString(TestContext context, String str, Object expected, Class checkClass) {
    Async async = context.async();
    ITypeHandler th = thf.getTypeHandler(checkClass, null);
    ResultObject<Object> ro = new ResultObject<>(null);
    th.fromStore(str, null, checkClass, res -> {
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
    checkEquals(context, expected, created);
  }

  protected void checkEquals(TestContext context, Object expected, Object created) {
    context.assertEquals(expected, created);
  }

  protected void checkTypeHandler(TestContext context, Class handleClass, Class expectedTypehandlerClass) {
    ITypeHandler th = thf.getTypeHandler(handleClass, null);
    context.assertEquals(expectedTypehandlerClass, th.getClass());
  }

}
