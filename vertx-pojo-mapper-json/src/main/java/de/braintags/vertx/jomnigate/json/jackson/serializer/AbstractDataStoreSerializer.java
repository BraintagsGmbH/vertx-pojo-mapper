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

import java.util.Collection;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import io.vertx.core.Future;

/**
 * Abstract Serializer which is used for fields, which are annotated by {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreSerializer<T> extends StdSerializer<T> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;
  private IDataStore datastore;

  /**
   * @param datastore
   *          the {@link IDataStore} to be used to resolve referenced instanced
   */
  public AbstractDataStoreSerializer(IDataStore datastore) {
    super((Class) null);
    this.datastore = datastore;
  }

  /**
   * @return the datastore
   */
  public IDataStore getDatastore() {
    return datastore;
  }

  /**
   * Store the given instance into the datastore
   * 
   * @param store
   * @param referencedObject
   * @return a future to be handled
   */
  @SuppressWarnings("unchecked")
  protected Future<IWriteResult> saveReferencedObject(IDataStore store, Object referencedObject) {
    Future<IWriteResult> f = Future.future();
    IWrite<Object> write = (IWrite<Object>) store.createWrite(referencedObject.getClass());
    write.add(referencedObject);
    write.save(f);
    return f;
  }

  /**
   * Store the given objects into the datastore
   * 
   * @param store
   * @param referencedObject
   * @return a future with the write result
   */
  @SuppressWarnings({ "unchecked" })
  protected Future<IWriteResult> saveReferencedObjects(IDataStore store, Collection referencedObjects) {
    IWrite<?> write = store.createWrite(referencedObjects.iterator().next().getClass());
    write.addAll(referencedObjects);
    Future<IWriteResult> future = Future.future();
    write.save(future);
    return future;
  }

  /**
   * Store the given objects into the datastore
   * 
   * @param store
   * @param referencedObject
   * @return a future with the write result
   */
  @SuppressWarnings({ "unchecked" })
  protected Future<IWriteResult> saveReferencedObjects(IDataStore store, Object[] referencedObjects) {
    IWrite write = store.createWrite(referencedObjects[0].getClass());
    for (Object rec : referencedObjects) {
      write.add(rec);
    }
    Future<IWriteResult> future = Future.future();
    write.save(future);
    return future;
  }

  /**
   * Get the full name of the annotated. For AnnotatedMember this will be className.fieldName for instance
   * 
   * @param an
   * @return
   */
  protected String getFullName(Annotated an) {
    if (an instanceof AnnotatedMember) {
      Class c = ((AnnotatedMember) an).getDeclaringClass();
      return c.getName() + "." + an.getName();
    } else {
      return an.getRawType().getName() + "." + an.getName();
    }
  }

}
