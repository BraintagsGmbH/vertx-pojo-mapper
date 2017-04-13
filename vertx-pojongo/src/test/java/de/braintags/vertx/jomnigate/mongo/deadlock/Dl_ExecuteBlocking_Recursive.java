/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.deadlock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.braintags.vertx.util.ResultObject;
import de.braintags.vertx.util.exception.InitException;
import io.vertx.core.Vertx;

/**
 * How to execute a blocking code inside a blocking code?
 * 
 * NOTE:
 * 
 * 
 * @author Michael Remme
 * 
 */
public class Dl_ExecuteBlocking_Recursive {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(Dl_ExecuteBlocking_Recursive.class);

  private static Vertx vertx = Vertx.vertx();

  public void execute() {
    System.out.println("STARTING EXECUTION: Thread: " + Thread.currentThread().getName());
    ResultObject<String> ro = new ResultObject(null);
    CountDownLatch latch = new CountDownLatch(1);

    vertx.<String> executeBlocking(future -> {
      System.out.println("EXECUTE BLOCKING1: Thread: " + Thread.currentThread().getName());
      try {
        String result = executeBlocking2();
        future.complete(result);
      } catch (Throwable e) {
        e.printStackTrace();
        future.fail(e);
      }
    }, false, result -> {
      if (result.failed()) {
        ro.setThrowable(result.cause());
      } else {
        System.out.println("finishing first: " + result.result());
        ro.setResult(result.result());
      }
      latch.countDown();
    });

    try {
      System.out.println("Wait for latch1 Thread: " + Thread.currentThread().getName() + " LATCH: " + latch);
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (ro.isError()) {
      ro.getThrowable().printStackTrace();
    } else {
      System.out.println("finished: " + ro.getResult());
    }
    vertx.close();
  }

  private String executeBlocking2() throws Throwable {
    System.out.println("START BLOCKING2: Thread: " + Thread.currentThread().getName());
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<String> ro = new ResultObject(null);

    vertx.<String> executeBlocking(future -> {
      System.out.println("EXECUTE BLOCKING2: Thread: " + Thread.currentThread().getName());
      future.complete("SUCCESS");
    }, res -> {
      System.out.println("got it: " + res.result());
      ro.setResult(res.result());
      latch.countDown();
    });

    System.out.println("Waiting for latch2: Thread: " + Thread.currentThread().getName() + " LATCH: " + latch);
    if (latch.await(1000, TimeUnit.MILLISECONDS)) {
      if (ro.isError()) {
        throw ro.getRuntimeException();
      } else {
        return ro.getResult();
      }
    } else {
      throw new InitException("timed out");
    }

  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Dl_ExecuteBlocking_Recursive main = new Dl_ExecuteBlocking_Recursive();
    main.execute();

  }

}
