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
package de.braintags.io.vertx.util;

/**
 * Utility methods dealing with Exceptions
 * 
 * @author Michael Remme
 * 
 */
public class ExceptionUtil {

  private ExceptionUtil() {
  }

  /**
   * This method creates a RuntimeException from the given Throwable or casts the Throwable as RuntimeException
   * 
   * @param e
   *          the {@link Throwable} to be examined
   * @return the exception itself, if it was a {@link RuntimeException} or a RuntimeException, which encapsulates the
   *         oroginal exception
   */
  public static RuntimeException createRuntimeException(Throwable e) {
    if (e instanceof RuntimeException)
      return (RuntimeException) e;
    if (e instanceof Error)
      throw (Error) e;
    return new RuntimeException(e);
  }

  /**
   * Gets the stacktrace of the given exception as <code>StringBuffer</code>
   * 
   * @param exception
   *          the exception from which to retrieve the stacktrace
   */
  public static StringBuilder getStackTrace(Throwable exception) {
    return appendStackTrace(exception, new StringBuilder());
  }

  /**
   * Gets the stacktrace of the given exception as <code>StringBuffer</code>
   * 
   * @param exception
   *          the exception from which to retrieve the stacktrace
   * @param stopString
   *          the String on which to stop adding new Exception trace
   */
  public static StringBuilder getStackTrace(Throwable exception, String stopString) {
    return appendStackTrace(exception, new StringBuilder(), stopString, -1);
  }

  /**
   * Gets the stacktrace of the given exception as <code>StringBuffer</code> The method will write maximum of ineCount
   * lines of the StackTrace
   * 
   * @param exception
   *          the exception from which to retrieve the stacktrace
   * @param lineCount
   *          the number of lines to be written
   */
  public static StringBuilder getStackTrace(Throwable exception, int lineCount) {
    return appendStackTrace(exception, new StringBuilder(), null, lineCount);
  }

  /**
   * Appends the stacktrace of the given exception into the given <code>StringBuffer</code>
   * 
   * @param exception
   *          the exception from which to retrieve the stacktrace
   * @param buffer
   *          the buffer into which to write the stacktrace
   * @param stopString
   *          the String on which to stop adding new Exception trace
   * @return the <code>StringBuffer</code> into which the content has been appended
   */
  public static StringBuilder appendStackTrace(Throwable exception, StringBuilder buffer, String stopString,
      int lineCount) {
    if (exception == null) {
      return buffer;
    }

    StackTraceElement[] stacks = exception.getStackTrace();

    for (int i = 0; i < stacks.length; i++) {
      if (lineCount > 0 && lineCount >= i)
        break;
      String line = stacks[i].toString();
      if (stopString != null && !stopString.isEmpty() && line.contains(stopString))
        break;
      buffer.append(line).append("\n");
    }

    return buffer;
  }

  /**
   * Appends the stacktrace of the given exception into the given <code>StringBuffer</code>
   * 
   * @param exception
   *          the exception from which to retrieve the stacktrace
   * @param buffer
   *          the buffer into which to write the stacktrace
   * @return the <code>StringBuffer</code> into which the content has been appended
   */
  public static StringBuilder appendStackTrace(Throwable exception, StringBuilder buffer) {
    return appendStackTrace(exception, buffer, null, -1);
  }

}