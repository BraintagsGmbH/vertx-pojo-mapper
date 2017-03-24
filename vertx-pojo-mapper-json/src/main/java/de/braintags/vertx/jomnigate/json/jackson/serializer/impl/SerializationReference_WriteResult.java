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
package de.braintags.vertx.jomnigate.json.jackson.serializer.impl;

import java.util.Iterator;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * SerializationReference stores information about references, which are written during serialization. It is used to
 * create the bridge between synchronous execution of jackson and async processing of the datastore on saving
 * instances.
 * 
 * @author Michael Remme
 *
 */
public class SerializationReference_WriteResult extends AbstractSerializerReference<IWriteResult> {
  private boolean asArrayMembers;

  /**
   * @param future
   * @param reference
   * @param asArrayMembers
   *          if true, then the write entries are expected to be written as array; otherwise only the first member is
   *          written
   */
  public SerializationReference_WriteResult(Future<IWriteResult> future, String reference, boolean asArrayMembers) {
    super(reference, future);
    this.asArrayMembers = asArrayMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference#resolveReference(de.braintags.vertx.
   * jomnigate.IDataStore, de.braintags.vertx.util.ResultObject)
   */
  @Override
  public final Future<Void> resolveReference(IDataStore<?, ?> datastore, ResultObject<String> ro) {
    try {
      String resolved = getResolvedReference(datastore);
      String result = ro.getResult().replace(getReference(), resolved);
      ro.setResult(result);
      return Future.succeededFuture();
    } catch (Exception e) {
      return Future.failedFuture(e);
    }
  }

  private String getResolvedReference(IDataStore<?, ?> datastore) {
    if (asArrayMembers) {
      Buffer buffer = Buffer.buffer();
      Iterator<IWriteEntry> it = getFuture().result().iterator();
      while (it.hasNext()) {
        // write a sequence like 23", "24", "25 -> cause the value to be replaced is stored within quotation marks
        // like "$REFERENCE_IDENTIFYER$555$
        if (buffer.length() > 0) {
          buffer.appendString("\", \"");
        }
        buffer.appendString(it.next().getId().toString());
      }
      return buffer.toString();
    } else {
      if (getFuture().result().size() != 1) {
        throw new IllegalArgumentException("Expected ONE instance for single object");
      }
      return getFuture().result().iterator().next().getId().toString();
    }
  }
}