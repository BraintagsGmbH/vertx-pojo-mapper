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
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Defines how the structural process of writing and reading of a field is performed. Technically there are existing 3
 * relevant IPropertyMapper:<br/>
 * <UL>
 * <LI/>pure property: the content of the given field is transformed from / into the needed format and written directly
 * as content of the {@link IStoreObject} or the mapper
 * <LI/>referenced: the content of the field is written / fetched somewehere else ( another table / column etc. ). When
 * writing into the {@link IDataStore}, this property is replaced by an identifyer. When reading, the object is loaded
 * and created from the other position again
 * <LI>embedded: the content of the field is written / read completely into / from the field as a substructure.
 * </UL>
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyMapper {

  /**
   * place the content of the given {@link IField} into the {@link IStoreObject}
   * 
   * @param entity
   *          the mapper object to be handled
   * @param storeObject
   *          the instance of {@link IStoreObject} where the content shall be placed
   * @param field
   *          the {@link IField} to be handled
   * @param handler
   *          the handler to be called
   */
  void intoStoreObject(Object entity, IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler);

  /**
   * This method reads the field value of the entity and converts the value into s suitable format for the datastore
   * 
   * @param entity
   *          the entity to read the value from
   * @param field
   *          the field to read the value
   * @param handler
   *          the handler to be informed about the result
   */
  void readForStore(Object entity, IField field, Handler<AsyncResult<Object>> handler);

  /**
   * fetch the content from the {@link IStoreObject} and store it inside the mapper
   * 
   * @param entity
   *          the mapper to be handled
   * @param storeObject
   *          the instance of {@link IStoreObject}, where the content shall be fetched from
   * @param field
   *          the {@link IField} to be handled
   * @param handler
   *          the handler to be called
   */
  void fromStoreObject(Object entity, IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler);

}
