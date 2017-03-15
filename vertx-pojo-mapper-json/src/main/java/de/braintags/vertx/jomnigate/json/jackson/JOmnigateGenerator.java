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
package de.braintags.vertx.jomnigate.json.jackson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;

import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * Special Generator which is enabled to track objects, which are annotated as {@link Referenced}.
 * The Generator is used during Serialization to collect referenced records and to fill the gap between synchrone
 * execution of jackson and async execution of datastore
 * 
 * @author Michael Remme
 *
 */
public class JOmnigateGenerator extends JsonGeneratorDelegate {
  private static final String REFERENCE_IDENTIFYER = "$REFERENCE_IDENTIFYER$%s$";
  private AtomicInteger counter = new AtomicInteger();
  private List<SerializationReference> referencedList = new ArrayList<>();

  /**
   * @param d
   */
  JOmnigateGenerator(JsonGenerator d) {
    super(d);
  }

  /**
   * adds the given {@link Future} as new entry and returns the reference, which will be replaced against the record
   * identifyer
   * 
   * @param future
   *          which includes the String, which will be replaced against the identifyer, which is generated here
   * @return
   */
  public String addEntry(Future<IWriteResult> future, boolean asArrayMembers) {
    String identifyer = String.format(REFERENCE_IDENTIFYER, String.valueOf(counter.incrementAndGet()));
    referencedList.add(new SerializationReference(future, identifyer, asArrayMembers));
    return identifyer;
  }

  /**
   * Creates a new {@link CompositeFuture} which contains all Futures for referenced instances
   * 
   * @return
   */
  public CompositeFuture createComposite() {
    List<Future> fl = new ArrayList();
    referencedList.stream().forEach(e -> fl.add(e.getFuture()));
    return CompositeFuture.all(fl);
  }

  /**
   * Returns the list of Futures, which were executed to store field members, which are {@link Referenced}
   * 
   * @return the referencedFutureList
   */
  public List<SerializationReference> getReferenceList() {
    return referencedList;
  }

  /**
   * SerializationReference stores information about references, which are written during serialization. It is used to
   * create the bridge between synchronous execution of jackson and async processing of the datastore on saving
   * instances.
   * 
   * @author Michael Remme
   *
   */
  public class SerializationReference {
    private Future<IWriteResult> future;
    private String reference;
    private boolean asArrayMembers;

    /**
     * @param future
     * @param reference
     * @param asArrayMembers
     *          if true, then the write entries are expected to be written as array; otherwise only the first member is
     *          written
     */
    public SerializationReference(Future<IWriteResult> future, String reference, boolean asArrayMembers) {
      this.future = future;
      this.reference = reference;
      this.asArrayMembers = asArrayMembers;
    }

    /**
     * Get the future, which was the result of storage of a referenced field content
     * 
     * @return the future
     */
    public Future<IWriteResult> getFuture() {
      return future;
    }

    /**
     * Get the reference, which was placed inside the generated json and which will be replaced by the real id from out
     * of the Future
     * 
     * @return the reference
     */
    public String getReference() {
      return reference;
    }

    /**
     * Gets the real identifyer(s), which will be replaced against the generated reference. This will be either a single
     * value or - if asArrayMembers was set to true - a cs-list of all records in the write result
     * 
     * @return
     */
    public String getResolvedReference() {
      if (asArrayMembers) {
        Buffer buffer = Buffer.buffer();
        Iterator<IWriteEntry> it = future.result().iterator();
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
        if (future.result().size() != 1) {
          throw new IllegalArgumentException("Expected ONE instance for single object");
        }
        return future.result().iterator().next().getId().toString();
      }
    }
  }
}