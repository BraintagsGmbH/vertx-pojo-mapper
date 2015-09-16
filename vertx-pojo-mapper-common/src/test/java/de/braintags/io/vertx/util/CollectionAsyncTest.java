/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class CollectionAsyncTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(CollectionAsyncTest.class);

  private TestCollectionAsync<String> tca1;
  private TestCollectionAsync<String> tca2;
  private TestCollectionAsync<String> tca3;
  private TestCollectionAsync<String> tcaEmpty;
  private TestCollectionAsync<String> tcaEmpty2;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    tca1 = new TestCollectionAsync<>("one", "two", "three", "four");
    tca2 = new TestCollectionAsync<>("one", "two", "three", "four");
    tca3 = new TestCollectionAsync<>("one", "two", "four", "five");
    tcaEmpty = new TestCollectionAsync<>(new ArrayList<String>());
    tcaEmpty2 = new TestCollectionAsync<>(new ArrayList<String>());
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#contains(java.lang.Object, io.vertx.core.Handler)}.
   */
  @Test
  public void testContainsTrue() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.contains("one", result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertTrue(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#contains(java.lang.Object, io.vertx.core.Handler)}.
   */
  @Test
  public void testContainsFalse() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.contains("five", result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#contains(java.lang.Object, io.vertx.core.Handler)}.
   */
  @Test
  public void testContainsEmptyList() {
    final CountDownLatch latch = new CountDownLatch(1);
    tcaEmpty.contains("one", result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for {@link de.braintags.io.vertx.util.AbstractCollectionAsync#toArray(io.vertx.core.Handler)}.
   */
  @Test
  public void testToArray() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.toArray(result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertEquals(tca1.size(), result1.result().length);
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for {@link de.braintags.io.vertx.util.AbstractCollectionAsync#toArray(io.vertx.core.Handler)}.
   */
  @Test
  public void testToArrayEmpty() {
    final CountDownLatch latch = new CountDownLatch(1);
    tcaEmpty.toArray(result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertEquals(tcaEmpty.size(), result1.result().length);
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync, io.vertx.core.Handler)}
   * .
   */
  @Test
  public void testContainsAll() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.containsAll(tca2, result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertTrue(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync, io.vertx.core.Handler)}
   * .
   */
  @Test
  public void testContainsAllFalse() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.containsAll(tca3, result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync, io.vertx.core.Handler)}
   * .
   */
  @Test
  public void testContainsAllEmptySource() {
    final CountDownLatch latch = new CountDownLatch(1);
    tcaEmpty.containsAll(tca3, result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync, io.vertx.core.Handler)}
   * .
   */
  @Test
  public void testContainsAllEmptyCompare() {
    final CountDownLatch latch = new CountDownLatch(1);
    tca1.containsAll(tcaEmpty, result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#containsAll(de.braintags.io.vertx.util.CollectionAsync, io.vertx.core.Handler)}
   * .
   */
  @Test
  public void testContainsAll_BothEmpty() {
    final CountDownLatch latch = new CountDownLatch(1);
    tcaEmpty2.containsAll(tcaEmpty, result1 -> {
      if (result1.failed()) {
        LOGGER.error("", result1.cause());
        fail(result1.cause().getMessage());
        latch.countDown();
      } else {
        try {
          assertFalse(result1.result());
        } finally {
          latch.countDown();
        }
      }
    });
    awaitLatch(latch);
  }

  /**
   * Test method for
   * {@link de.braintags.io.vertx.util.AbstractCollectionAsync#addAll(de.braintags.io.vertx.util.CollectionAsync)}.
   */
  @Test
  public void testAddAll() {
    final CountDownLatch latch = new CountDownLatch(1);
    try {
      tca1.addAll(tca2);
      fail("This should throw an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // test is ok
    } catch (Exception e) {
      fail("This should throw an UnsupportedOperationException");
    } finally {
      latch.countDown();
    }

    awaitLatch(latch);
  }

  private void awaitLatch(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      LOGGER.error("", e);
    }
  }

}
