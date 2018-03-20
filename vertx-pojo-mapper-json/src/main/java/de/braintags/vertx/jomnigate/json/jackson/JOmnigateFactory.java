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

import java.io.IOException;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.json.jackson.serializer.JOmnigateGenerator;
import de.braintags.vertx.util.ExceptionUtil;

/**
 * An extension of {@link JsonFactory} to allow ReferencedSerializers and Embedded Serializers to store Future, which
 * are executed to serialize fields, which are annotated as {@link Referenced} or {@link Embedded}
 *
 * @author Michael Remme
 *
 */
public class JOmnigateFactory extends MappingJsonFactory {
  private final JsonDatastore datastore;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param src
   * @param mapper
   */
  public JOmnigateFactory(final JsonDatastore datastore, final JsonFactory src, final ObjectMapper mapper) {
    super(src, mapper);
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.fasterxml.jackson.core.JsonFactory#_createGenerator(java.io.Writer,
   * com.fasterxml.jackson.core.io.IOContext)
   */
  @Override
  protected JsonGenerator _createGenerator(final Writer out, final IOContext ctxt) throws IOException {
    return new JOmnigateGenerator(datastore, super._createGenerator(out, ctxt), (SegmentedStringWriter) out);
  }

  /**
   * Create a new instance of JOmnigateGenerator
   *
   * @param datastore
   *          the datastore to be used
   * @return
   */
  public static final JOmnigateGenerator createGenerator(final JsonDatastore datastore) {
    try {
      ObjectMapper mapper = datastore.getJacksonMapper();
      SegmentedStringWriter sw = new SegmentedStringWriter(mapper.getFactory()._getBufferRecycler());
      return (JOmnigateGenerator) mapper.getFactory().createGenerator(sw);
    } catch (Exception e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  @Override
  public JsonFactory copy() {
    return new MappingJsonFactory(this, null);
  }

}
