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

package de.braintags.io.vertx.pojomapper.typehandler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.mapping.IField;

/**
 * The ITypehandler is responsible to change original values into the required format of the used datastore
 * 
 * @author Michael Remme
 * 
 */

public interface ITypeHandler {

  /**
   * Returned by method {@link #matches(IField)} to specify that the current typehandler won't handle the given field
   */
  public static final short MATCH_NONE = 0;

  /**
   * Returned by method {@link #matches(IField)} to specify that the current typehandler handles the given field in a
   * minor way. For instance, if the class of the field is not direct the class, which the typehandler deals with, but
   * an instance of
   */
  public static final short MATCH_MINOR = 1;

  /**
   * Returned by method {@link #matches(IField)} to specify that the current typehandler handles the given field in a
   * major way. For instance, if the class of the field is the direct class
   */
  public static final short MATCH_MAJOR = 2;

  /**
   * This method is called when a value is read from a field of a mapped object and must change the value into the
   * needed format and type
   * 
   * @param source
   *          the source, which was read from the datastore
   * @param field
   *          the underlaying field information
   * @param cls
   *          if a caller can't access some {@link IField} information, then the resulting class should be defined here
   * @param resultHandler
   *          the method will store the result inside the {@link ITypeHandlerResult}
   */
  void fromStore(Object source, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> resultHandler);

  /**
   * This method is called when an object shall be persisted into the datastore and shall change the value into the
   * needed format of the datastore
   * 
   * @param source
   *          the source, which was read from the java instance
   * @param field
   *          the underlaying field information
   * @param resultHandler
   *          the method will store the result inside the {@link ITypeHandlerResult}
   */
  void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler);

  /**
   * Checks wether the given {@link IField} is matching the criteria in the current instance. The method returns a
   * graded result, one of {@link #MATCH_NONE}, {@link #MATCH_MINOR} or {@link #MATCH_MAJOR}
   * 
   * @param field
   *          the field to be checked
   * @return 0 ( zero ) if the Typ
   */
  public short matches(IField field);

  /**
   * Checks wether the given {@link Class} is matching the criteria in the current instance. The method returns a graded
   * result, one of {@link #MATCH_NONE}, {@link #MATCH_MINOR} or {@link #MATCH_MAJOR}
   * 
   * @param cls
   *          the Class to be checked
   * @return 0 ( zero ) if the Typ
   */
  public short matches(Class<?> cls);

}
