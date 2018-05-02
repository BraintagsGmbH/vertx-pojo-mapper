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
package de.braintags.vertx.jomnigate.json.typehandler.handler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Deals all fields, which contain {@link Collection} content, which are NOT annotated as {@link Referenced} or
 * {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class CollectionTypeHandler extends AbstractTypeHandler {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(CollectionTypeHandler.class);

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public CollectionTypeHandler(final ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Collection.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(final Annotation annotation) {
    return annotation == null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void fromStore(final Object source, final IProperty field, final Class<?> cls,
      final Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(null, resultHandler);
    } else if (((JsonArray) source).isEmpty()) {
      success(field.getMapper().getObjectFactory().createCollection(field), resultHandler);
    } else {
      CompositeFuture cf = handleObjectsFromStore(field, (JsonArray) source);
      cf.setHandler(result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          Collection coll = field.getMapper().getObjectFactory().createCollection(field);
          coll.addAll(cf.list());
          success(coll, resultHandler);
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture handleObjectsFromStore(final IProperty field, final JsonArray source) {
    List<Future> fl = new ArrayList<>();
    ITypeHandler subHandler = field.getSubTypeHandler();
    for (int i = 0; i < source.size(); i++) {
      fl.add(i, handleObjectFromStore(field, subHandler, source.getValue(i)));
    }
    return CompositeFuture.all(fl);
  }

  /**
   * Create one instance of the {@link Collection} and return the Future
   * 
   * @param o
   *          the object from the store
   * @param subHandler
   *          the subhandler to be used
   * @param coll
   *          the collection to be filled
   * @param field
   *          the field, where the Collection stays in
   * @param resultHandler
   *          the handler to be informed
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Future handleObjectFromStore(final IProperty field, final ITypeHandler subHandler, final Object o) {
    Future f = Future.future();
    if (subHandler != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("subtypehandler: " + subHandler.getClass().getName());
      }
      subHandler.fromStore(o, field, field.getSubClass(), tmpResult -> {
        if (tmpResult.failed()) {
          f.fail(tmpResult.cause());
        } else {
          f.complete(tmpResult.result().getResult());
        }
      });
    } else {
      f.complete(o);
    }
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public final void intoStore(final Object source, final IProperty field, final Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(null, resultHandler);
    } else if (((Collection<?>) source).isEmpty()) {
      success(encodeResultArray(new JsonArray()), resultHandler);
    } else {
      Collection coll = (Collection) source;
      CompositeFuture cf = encodeSubValues(coll, field);
      cf.setHandler(cfh -> {
        if (cfh.failed()) {
          fail(cfh.cause(), resultHandler);
        } else {
          try {
            success(encodeResultArray(transferEncodedResults(cf)), resultHandler);
          } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
          }
        }
      });
    }
  }

  /**
   * Transfers the results inside the {@link CompositeFuture} into a JsonArray.
   * 
   * @param cf
   *          a CompositeFuture, where the results are of type {@link ITypeHandlerResult}
   * @return an instance which contains the encoded results of the CompositeFuture.
   */
  protected final JsonArray transferEncodedResults(final CompositeFuture cf) {
    JsonArray jsonArray = new JsonArray();
    for (Object thr : cf.list()) {
      Object value = ((ITypeHandlerResult) thr).getResult();
      if (value == null) {
        jsonArray.addNull();
      } else {
        jsonArray.add(value);
      }
    }
    return jsonArray;
  }

  /**
   * Converts the JsonArray into an adequate format which can be stored by the datastore
   * 
   * @param result
   * @return
   */
  protected Object encodeResultArray(final JsonArray result) {
    return result;
  }

  protected CompositeFuture encodeSubValues(final Collection coll, final IProperty field) {
    List<Future> fl = new ArrayList<>();
    ITypeHandler subHandler = field.getSubTypeHandler();
    // no generics were defined, so that subhandler could not be defined from mapping
    boolean determineSubhandler = subHandler == null;
    Class<?> valueClass = null;
    for (Object value : coll) {
      if (determineSubhandler) {
        boolean valueClassChanged = valueClass != null && value.getClass() != valueClass;
        valueClass = value.getClass();
        if (subHandler == null || valueClassChanged) {
          subHandler = getSubTypeHandler(value.getClass(), field.getEmbedRef());
          // TODO could it be useful to write the class of the value into the field, to restore it proper from
          // datastore?
        }
      }
      fl.add(encodeSubValue(field, subHandler, value));
    }
    return CompositeFuture.all(fl);
  }

  private Future encodeSubValue(final IProperty field, final ITypeHandler subHandler, final Object value) {
    Future f = Future.future();
    subHandler.intoStore(value, field, f.completer());
    return f;
  }

}
