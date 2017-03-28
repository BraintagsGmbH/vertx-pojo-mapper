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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Special Generator which is enabled to track objects, which are annotated as {@link Referenced} or {@link Embedded}.
 * The Generator is used during Serialization to collect referenced records and to fill the gap between synchrone
 * execution of jackson and async execution of datastore
 * 
 * @author Michael Remme
 *
 */
public class JOmnigateGenerator extends JsonGeneratorDelegate {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JOmnigateGenerator.class);

  private static final String REFERENCE_IDENTIFYER = "$REFERENCE_IDENTIFYER$%s$";
  private AtomicInteger counter = new AtomicInteger();
  private List<ISerializationReference> referencedList = new ArrayList<>();
  private JOmnigateGenerator parentGenerator;
  private SegmentedStringWriter writer;
  private JsonDatastore datastore;

  /**
   * @param d
   */
  public JOmnigateGenerator(JsonDatastore datastore, JsonGenerator d, SegmentedStringWriter writer) {
    super(d);
    this.writer = writer;
    this.datastore = datastore;
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
    if (parentGenerator != null) {
      return parentGenerator.addEntry(future, asArrayMembers);
    } else {
      String identifyer = String.format(REFERENCE_IDENTIFYER, String.valueOf(counter.incrementAndGet()));
      addEntry(ISerializationReference.createSerializationReference(future, identifyer, asArrayMembers));
      return identifyer;
    }
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
    if (parentGenerator != null) {
      return parentGenerator.addEntry(future);
    } else {
      String identifyer = String.format(REFERENCE_IDENTIFYER, String.valueOf(counter.incrementAndGet()));
      addEntry(ISerializationReference.createSerializationReference(future, identifyer, this));
      return identifyer;
    }
  }

  private void addEntry(ISerializationReference ref) {
    List<ISerializationReference> list = referencedList == null ? new ArrayList() : new ArrayList(referencedList);
    list.add(ref);
    referencedList = list;
  }

  /**
   * Creates a new {@link CompositeFuture} which contains all Futures for referenced instances
   * 
   * @return
   */
  private CompositeFuture createComposite() {
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
    return Collections.unmodifiableList(referencedList);
  }

  /**
   * Get the result of serialization. This includes resolvement of referenced or embedded elements
   * 
   * @param handler
   */
  public void getResult(Handler<AsyncResult<String>> handler) {
    String generatedSource = getWriter().getAndClear();
    if (getReferenceList().isEmpty()) {
      handler.handle(Future.succeededFuture(generatedSource));
    } else {
      CompositeFuture cf = createComposite();
      cf.setHandler(res -> {
        if (res.failed()) {
          handler.handle(Future.failedFuture(res.cause()));
        } else {
          referenceLoop(datastore, generatedSource, handler);
        }
      });
    }
  }

  /**
   * @param jgen
   * @param generatedSource
   * @param handler
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void referenceLoop(IDataStore datastore, String generatedSource, Handler<AsyncResult<String>> handler) {
    try {
      ResultObject<String> ro = new ResultObject<>(null);
      ro.setResult(generatedSource);
      List<Future> fl = new ArrayList<>();
      List<ISerializationReference> list = getReferenceList();
      for (ISerializationReference ref : list) {
        fl.add(ref.resolveReference(datastore, ro));
      }
      CompositeFuture cf = CompositeFuture.all(fl);
      cf.setHandler(result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          String modSource = ro.getResult();
          validateResult(handler, modSource);
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * @param handler
   * @param newSource
   */
  private void validateResult(Handler<AsyncResult<String>> handler, String newSource) {
    if (newSource.contains("$REFERENCE_IDENTIFYER")) {
      handler.handle(
          Future.failedFuture(new IllegalArgumentException("references not completely resolved: " + newSource)));
    } else {
      handler.handle(Future.succeededFuture(newSource));
    }
  }

  private SegmentedStringWriter getWriter() {
    return writer;
  }

}