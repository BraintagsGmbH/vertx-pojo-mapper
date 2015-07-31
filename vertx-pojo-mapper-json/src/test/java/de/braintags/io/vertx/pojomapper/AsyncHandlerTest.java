/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.braintags.io.vertx.util.CounterObject;

public class AsyncHandlerTest {
  List<String> stringList = new ArrayList<String>();

  @Before
  public void setUp() throws Exception {
    stringList = new ArrayList<String>();
    for (int i = 0; i < 100; i++) {
      stringList.add("mein String mit der Zahl " + i);
    }
  }

  @Test
  public void test() {
    List<String> secondList = new ArrayList<String>();
    CounterObject co = new CounterObject(stringList.size());
    int counter = 0;
    for (String string : stringList) {
      handleOneString(counter++, string, result -> {
        if (result.failed()) {
          result.cause().printStackTrace();
        } else {
          secondList.add(result.result());
          if (co.reduce())
            Assert.assertEquals(stringList, secondList);
        }
      });

    }

  }

  void handleOneString(int counter, String string, Handler<AsyncResult<String>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(string));
  }

}
