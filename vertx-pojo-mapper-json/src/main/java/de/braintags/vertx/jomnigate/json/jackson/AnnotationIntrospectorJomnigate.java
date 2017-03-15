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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.PackageVersion;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Encoder;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.geo.GeoPointDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.EncoderSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.geo.GeoPointSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedArraySerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedCollectionSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedObjectSerializer;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class AnnotationIntrospectorJomnigate extends NopAnnotationIntrospector implements Versioned {
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  private IDataStore datastore;

  /**
   * 
   */
  public AnnotationIntrospectorJomnigate(IDataStore datastore) {
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.AnnotationIntrospector#version()
   */
  @Override
  public Version version() {
    return PackageVersion.VERSION;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.fasterxml.jackson.databind.AnnotationIntrospector#findSerializer(com.fasterxml.jackson.databind.introspect.
   * Annotated)
   */
  @Override
  public Object findSerializer(Annotated am) {
    if (am.hasAnnotation(Referenced.class)) {
      return findReferencedSerializer(am);
    } else if (am.hasAnnotation(Encoder.class)) {
      return new EncoderSerializer(datastore, am);
    } else if (am.getType() != null && am.getType().isTypeOrSubTypeOf(GeoPoint.class)) {
      return new GeoPointSerializer(datastore);
    }
    return super.findSerializer(am);
  }

  /**
   * We are defining serializer of Maps, which are annotated as {@link Referenced}
   */
  @Override
  public Object findContentSerializer(Annotated am) {
    if (am.getType().isMapLikeType() && am.hasAnnotation(Referenced.class)) {
      // values of a map, which is annotated Referenced
      return new ReferencedObjectSerializer(datastore);
    }
    return super.findContentSerializer(am);
  }

  /**
   * @param am
   * @return
   */
  private Object findReferencedSerializer(Annotated am) {
    JavaType jt = am.getType();
    if (jt.isArrayType()) {
      checkEntity(am, am.getType().getContentType().getRawClass());
      return new ReferencedArraySerializer(datastore);
    } else if (jt.isCollectionLikeType()) {
      checkEntity(am, am.getType().getContentType().getRawClass());
      return new ReferencedCollectionSerializer(datastore);
    } else if (jt.isMapLikeType()) {
      checkEntity(am, am.getType().getContentType().getRawClass());
      // this is done by using the default serializer with a separate content serializer in findContentSerializer
      return null;
    } else if (jt.isEnumType()) {
      throw new UnsupportedOperationException("referenced Enum is not supported");
    } else {
      checkEntity(am, am.getType().getRawClass());
      return new ReferencedObjectSerializer(datastore);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.fasterxml.jackson.databind.AnnotationIntrospector#findDeserializer(com.fasterxml.jackson.databind.introspect.
   * Annotated)
   */
  @Override
  public Object findDeserializer(Annotated am) {
    if (am.getType() != null && am.getType().isTypeOrSubTypeOf(GeoPoint.class)) {
      return new GeoPointDeserializer(datastore, am);
    }
    return super.findDeserializer(am);
  }

  private void checkEntity(Annotated am, Class<?> mapperClass) {
    if (mapperClass.getAnnotation(Entity.class) == null) {
      throw new MappingException("referenced entities must be annotated as Entity: " + am);
    }
  }

}
