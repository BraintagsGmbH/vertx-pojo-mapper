/*
 * Copyright 2014 Red Hat, Inc.
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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.util.ClassUtil;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestReflection {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Test
  public void testGenericFieldTypeResolution() throws Exception {
    Field field = Super1.class.getDeclaredField("field");
    Class<?> fieldClass = field.getType();
    TypeVariable<?> tv = (TypeVariable<?>) field.getGenericType();
    Class<?> typeArgument = ClassUtil.getTypeArgument(Sub.class, tv);
    System.out.println("fieldClass: " + fieldClass);
    System.out.println("TypeVariable: " + tv);
    System.out.println("typeArgument: " + typeArgument);
    assertEquals("Wrong Result", Integer.class, typeArgument);
  }

  private static class Super1<T extends Object> {
    private T field;
  }

  private static class Super2<T extends Serializable> extends Super1<T> {
  }

  private static class Super3<T extends Number> extends Super2<T> {
  }

  private static class Sub extends Super3<Integer> {
  }

  private static class Author {
    private Collection<Book> books;
  }

  private static class Authors extends HashSet<Author> {
    // Can contain utils methods
  }

  private static class WritingTeam extends Authors {
  }

  @SuppressWarnings("unused")
  private static class Book {
    private Authors authors;

    private Set<Author> authorsSet;
  }

}
