/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.jackson.serializer;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.json.jackson.serializer.impl.SerializationReference_Entity;
import de.braintags.vertx.jomnigate.json.jackson.serializer.impl.SerializationReference_WriteResult;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.Future;

/**
 * ISerializationReference stores information about references, which are written during serialization. It is used to
 * create the bridge between synchronous execution of jackson and async processing of the datastore on saving
 * instances.
 * 
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the Future, which contains the value to be handled
 */
public interface ISerializationReference<T> {

  public static ISerializationReference createSerializationReference(Future<Object> future, String reference,
      JOmnigateGenerator generator) {
    return new SerializationReference_Entity(future, reference, generator);
  }

  public static ISerializationReference createSerializationReference(Future<IWriteResult> future, String reference,
      boolean asArrayMembers) {
    return new SerializationReference_WriteResult(future, reference, asArrayMembers);
  }

  /**
   * Get the underlaying Future
   * 
   * @return
   */
  Future<T> getFuture();

  /**
   * Get the reference, which was placed inside the generated json and which will be replaced by the real id from out
   * of the Future
   * 
   * @return the reference
   */
  String getReference();

  /**
   * Method replaces the reference, which was written by a previous serializer, against the result object
   * 
   * @param datastore
   * @param ro
   *          the ResultObject, which contains the String to be modified and where the modified content will be stored
   *          again
   * @return
   */
  Future<Void> resolveReference(IDataStore<?, ?> datastore, ResultObject<String> ro);

}