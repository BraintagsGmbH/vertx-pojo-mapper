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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;

import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

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
  private List<ISerializationReference> referencedList = new ArrayList<>();

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
    referencedList.add(ISerializationReference.createSerializationReference(future, identifyer, asArrayMembers));
    return identifyer;
  }

  /**
   * adds the given {@link Future} as new entry and returns the reference, which will be replaced against the record
   * identifyer
   * 
   * @param future
   *          which includes the String, which will be replaced against the identifyer, which is generated here
   * @return
   */
  public String addEntry(Future<Object> future) {
    String identifyer = String.format(REFERENCE_IDENTIFYER, String.valueOf(counter.incrementAndGet()));
    referencedList.add(ISerializationReference.createSerializationReference(future, identifyer));
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
  public List<ISerializationReference> getReferenceList() {
    return referencedList;
  }
}