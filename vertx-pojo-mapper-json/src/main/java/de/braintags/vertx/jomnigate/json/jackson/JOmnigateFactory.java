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
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.braintags.vertx.jomnigate.annotation.field.Referenced;

/**
 * An extension of {@link JsonFactory} to allow ReferencedSerializers to store Future, which are executed to serialize
 * fields, which are annotated as {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */
public class JOmnigateFactory extends MappingJsonFactory {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public JOmnigateFactory() {
  }

  /**
   * @param mapper
   */
  public JOmnigateFactory(ObjectMapper mapper) {
    super(mapper);
  }

  /**
   * @param src
   * @param mapper
   */
  public JOmnigateFactory(JsonFactory src, ObjectMapper mapper) {
    super(src, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.core.JsonFactory#_createGenerator(java.io.Writer,
   * com.fasterxml.jackson.core.io.IOContext)
   */
  @Override
  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
    return new JOmnigateGenerator(super._createGenerator(out, ctxt));
  }

}
