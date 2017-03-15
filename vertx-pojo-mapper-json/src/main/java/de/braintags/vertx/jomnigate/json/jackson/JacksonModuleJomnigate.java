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

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.jaxb.PackageVersion;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;

/**
 * Extension module to react to certain datatypes like {@link GeoPoint} and annotations like {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */
public class JacksonModuleJomnigate extends SimpleModule {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * Enumeration that defines how we use JAXB Annotations: either
   * as "primary" annotations (before any other already configured
   * introspector -- most likely default JacksonAnnotationIntrospector) or
   * as "secondary" annotations (after any other already configured
   * introspector(s)).
   * <p>
   * Default choice is <b>PRIMARY</b>
   * <p>
   * Note that if you want to use JAXB annotations as the only annotations,
   * you must directly set annotation introspector by calling
   * {@link com.fasterxml.jackson.databind.ObjectMapper#setAnnotationIntrospector}.
   */
  public enum Priority {
    PRIMARY,
    SECONDARY;
  }

  /**
   * Priority to use when registering annotation introspector: default
   * value is {@link Priority#PRIMARY}.
   */
  protected Priority _priority = Priority.PRIMARY;
  private IDataStore datastore;

  public JacksonModuleJomnigate(IDataStore datastore) {
    super(PackageVersion.VERSION);
    if (datastore == null) {
      throw new NullPointerException("need an instance of IDataStore");
    }
    this.datastore = datastore;
  }

  @Override
  public void setupModule(SetupContext context) {
    AnnotationIntrospectorJomnigate intr = new AnnotationIntrospectorJomnigate(datastore);
    switch (_priority) {
    case PRIMARY:
      context.insertAnnotationIntrospector(intr);
      break;
    case SECONDARY:
      context.appendAnnotationIntrospector(intr);
      break;
    }

    context.addBeanDeserializerModifier(new ReferencedBeanDeserializerModifyer(datastore));
  }

  /*
   * /**********************************************************
   * /* Configuration
   * /**********************************************************
   */

  /**
   * Method for defining whether JAXB annotations should be added
   * as primary or secondary annotations (compared to already registered
   * annotations).
   * <p>
   * NOTE: method MUST be called before registering the module -- calling
   * afterwards will not have any effect on previous registrations.
   */
  public JacksonModuleJomnigate setPriority(Priority p) {
    _priority = p;
    return this;
  }

  public Priority getPriority() {
    return _priority;
  }

}
