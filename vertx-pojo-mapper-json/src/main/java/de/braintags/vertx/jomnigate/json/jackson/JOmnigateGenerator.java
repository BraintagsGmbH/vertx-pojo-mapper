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
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.serializer.ISerializationReference;
import edu.emory.mathcs.backport.java.util.Collections;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

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
  private JOmnigateGenerator parentGenerator;
  private SegmentedStringWriter writer;
  private JsonDatastore datastore;

  /**
   * @param d
   */
  JOmnigateGenerator(JsonDatastore datastore, JsonGenerator d, SegmentedStringWriter writer) {
    super(d);
    this.writer = writer;
  }

  public void getResult(Handler<AsyncResult<String>> handler) {
    String js = jgen.getWriter().getAndClear();
    jgen.resolveReferences(datastore, js, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        storeJson(res.result(), handler);
      }
    });

  }

  private SegmentedStringWriter getWriter() {
    return writer;
  }

  /**
   * Set the parent {@link JOmnigateGenerator} to share the counter
   * 
   * @param parent
   */
  public void setParentJomnigateGenerator(JOmnigateGenerator parent) {
    parentGenerator = parent;
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
    return Collections.unmodifiableList(referencedList);
  }

  /**
   * if the generator contains reference information, they are replaced against their real value
   * 
   * @param datastore
   * @param generatedSource
   * @param handler
   */
  public void resolveReferences(IDataStore datastore, String generatedSource, Handler<AsyncResult<String>> handler) {
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
  private void referenceLoop(IDataStore datastore, String generatedSource, Handler<AsyncResult<String>> handler) {
    String newSource = generatedSource;
    try {
      List<Future> fl = new ArrayList<>();
      List<ISerializationReference> list = getReferenceList();
      for (ISerializationReference ref : list) {
        fl.add(ref.resolveReference(datastore, newSource));
      }
      CompositeFuture cf = CompositeFuture.all(fl);
      cf.setHandler(result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          String modSource = cf.size() == 0 ? newSource : cf.resultAt(cf.size() - 1);
          validateResult(handler, modSource);
        }
      });
    } catch (Exception e) {
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

}