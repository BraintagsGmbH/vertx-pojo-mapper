/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.typehandler;

import java.lang.annotation.Annotation;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IField;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * The ITypehandler is responsible to change original values into the required format of the used datastore. Cause
 * {@link ITypeHandler} can have stateful information based on the {@link IField}, where an {@link ITypeHandler} is
 * belonging to, each IField is getting its own clone
 * 
 * @author Michael Remme
 * 
 */

public interface ITypeHandler extends Cloneable {

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
   * @return MATCH_NONE if the field is not handled by the current instance, MATCH_MINOR if it is fitting partially (
   *         like a subclass for instance ) or MATCH_MAJOR if it is highly fitting, like the exact class for instance
   */
  public short matches(IField field);

  /**
   * Checks wether the given {@link Class} is matching the criteria in the current instance. The method returns a graded
   * result, one of {@link #MATCH_NONE}, {@link #MATCH_MINOR} or {@link #MATCH_MAJOR}
   * 
   * @param cls
   *          the Class to be checked
   * @param embedRef
   *          an annotation of type {@link Referenced} or {@link Embedded} or NULL. ITypeHandler can react to this
   *          information
   * 
   * @return 0 ( zero ) if the Typ
   */
  public short matches(Class<?> cls, Annotation embedRef);

  /**
   * Get the {@link ITypeHandlerFactory} where the current instance is belonging to.
   * 
   * @return
   */
  public ITypeHandlerFactory getTypeHandlerFactory();

  /**
   * Some ITypeHandler, which are processing List, Array, Map etc., are using sub handler to process the values of the
   * List etc. Those handler must not come from out of the same {@link ITypeHandlerFactory}. For example can decide a
   * CollectionHandler, that it serializes all children into JSon and thus is using the JSonTypeHandlerFactory to define
   * the typehandlers
   * 
   * @param subClass
   *          the class to be examined
   * @param embedRef
   *          an annotation of type {@link Referenced} or {@link Embedded} or NULL. ITypeHandler can react to this
   *          information
   * @return a valid {@link ITypeHandler}
   */
  public ITypeHandler getSubTypeHandler(Class<?> subClass, Annotation embedRef);

  /**
   * Generates a new copy of this instance.
   *
   * @return a copy of this instance
   *
   * @see Object#clone()
   */
  public Object clone();
}
